/**
 * 
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.apdplat.platform.filter;

import org.apdplat.platform.log.APDPlatLogger;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.apdplat.platform.log.APDPlatLoggerFactory;

/**
 * Provides GZIP compression of responses.
 * <p/>
 * See the filter-mappings.xml entry for the gzip filter for the URL patterns
 * which will be gzipped. At present this includes .jsp, .js and .css.
 *
 * @author <a href="mailto:gluck@thoughtworks.com">Greg Luck</a>
 * @author <a href="mailto:amurdoch@thoughtworks.com">Adam Murdoch</a>
 * @version $Id: GzipFilter.java 80 2006-05-14 03:16:22Z gregluck $
 */

public class GzipFilter extends Filter {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(GzipFilter.class);
    /**
     * Performs initialisation.
     */
    @Override
    protected void doInit() throws Exception {
    }

    /**
     * A template method that performs any Filter specific destruction tasks.
     * Called from {@link #destroy()}
     */
    @Override
    protected void doDestroy() {
        //noop
    }

    /**
     * Performs the filtering for a request.
     */
    @Override
    protected void doFilter(final HttpServletRequest request, final HttpServletResponse response,
                            final FilterChain chain) throws Exception {
        try{
            if (!isIncluded(request) && acceptsEncoding(request, "gzip")) {
                // Client accepts zipped content
                if (LOG.isDebugEnabled()) {
                    LOG.debug(request.getRequestURL() + ". Writing with gzip compression");
                }

                // Create a gzip stream
                final ByteArrayOutputStream compressed = new ByteArrayOutputStream();
                final GenericResponseWrapper wrapper;
                try (GZIPOutputStream gzout = new GZIPOutputStream(compressed)) {
                    wrapper = new GenericResponseWrapper(response, gzout);
                    chain.doFilter(request, wrapper);
                    wrapper.flush();
                }

                //Saneness checks
                byte[] compressedBytes = compressed.toByteArray();
                boolean shouldGzippedBodyBeZero = ResponseUtil.shouldGzippedBodyBeZero(compressedBytes, request);
                boolean shouldBodyBeZero = ResponseUtil.shouldBodyBeZero(request, wrapper.getStatus());
                if (shouldGzippedBodyBeZero || shouldBodyBeZero) {
                    compressedBytes = new byte[0];
                }

                // Write the zipped body
                ResponseUtil.addGzipHeader(response);
                response.setContentLength(compressedBytes.length);


                response.getOutputStream().write(compressedBytes);
            } else {
                // Client does not accept zipped content - don't bother zipping
                if (LOG.isDebugEnabled()) {
                    LOG.debug(request.getRequestURL()
                            + ". Writing without gzip compression because the request does not accept gzip.");
                }
                chain.doFilter(request, response);
            }
        }catch(Exception e){
            LOG.info("增加zip压缩失败："+request.getRequestURI());
        }
    }



    /**
     * Checks if the request uri is an include.
     * These cannot be gzipped.
     */
    private boolean isIncluded(final HttpServletRequest request) {
        final String uri = (String) request.getAttribute("javax.servlet.include.request_uri");
        final boolean includeRequest = !(uri == null);

        if (includeRequest && LOG.isDebugEnabled()) {
            LOG.debug(request.getRequestURL() + " resulted in an include request. This is unusable, because" +
                    "the response will be assembled into the overrall response. Not gzipping.");
        }
        return includeRequest;
    }

    /**
     * Determine whether the user agent accepts GZIP encoding. This feature is part of HTTP1.1.
     * If a browser accepts GZIP encoding it will advertise this by including in its HTTP header:
     * <p/>
     * <code>
     * Accept-Encoding: gzip
     * </code>
     * <p/>
     * Requests which do not accept GZIP encoding fall into the following categories:
     * <ul>
     * <li>Old browsers, notably IE 5 on Macintosh.
     * <li>Internet Explorer through a proxy. By default HTTP1.1 is enabled but disabled when going
     * through a proxy. 90% of non gzip requests seen on the Internet are caused by this.
     * </ul>
     * As of September 2004, about 34% of Internet requests do not accept GZIP encoding.
     *
     * @param request
     * @return true, if the User Agent request accepts GZIP encoding
     */
    @Override
    protected boolean acceptsGzipEncoding(HttpServletRequest request) {
        return acceptsEncoding(request, "gzip");
    }


}


class GenericResponseWrapper extends HttpServletResponseWrapper implements Serializable {

    private static final long serialVersionUID = -5976708169031065498L;

    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(GenericResponseWrapper.class);
    private int statusCode = SC_OK;
    private int contentLength;
    private String contentType;
    private final List headers = new ArrayList();
    private final List cookies = new ArrayList();
    private ServletOutputStream outstr;
    private PrintWriter writer;

    /**
     * Creates a GenericResponseWrapper
     */
    public GenericResponseWrapper(final HttpServletResponse response, final OutputStream outstr) {
        super(response);
        this.outstr = new FilterServletOutputStream(outstr);
    }

    /**
     * Gets the outputstream.
     */
    @Override
    public ServletOutputStream getOutputStream() {
        return outstr;
    }

    /**
     * Sets the status code for this response.
     */
    @Override
    public void setStatus(final int code) {
        statusCode = code;
        super.setStatus(code);
    }


    /**
     * Returns the status code for this response.
     *
     */
    @Override
    public int getStatus() {
        return statusCode;
    }

    /**
     * Sets the content length.
     */
    @Override
    public void setContentLength(final int length) {
        this.contentLength = length;
        super.setContentLength(length);
    }

    /**
     * Gets the content length.
     */
    public int getContentLength() {
        return contentLength;
    }

    /**
     * Sets the content type.
     */
    @Override
    public void setContentType(final String type) {
        this.contentType = type;
        super.setContentType(type);
    }

    /**
     * Gets the content type.
     */
    @Override
    public String getContentType() {
        return contentType;
    }

    /**
     * Gets the print writer.
     */
    @Override
    public PrintWriter getWriter() {
        if (writer == null) {
            try {
                writer = new PrintWriter(new OutputStreamWriter(outstr, "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                LOG.error("gzip构造PrintWriter失败："+ex);                
            }
        }
        return writer;
    }

    /**
     * Adds a header.
     */
    @Override
    public void addHeader(final String name, final String value) {
        final String[] header = new String[]{name, value};
        headers.add(header);
        super.addHeader(name, value);
    }

    /**
     * @see #addHeader
     */
    @Override
    public void setHeader(final String name, final String value) {
        addHeader(name, value);
    }

    /**
     * Gets the headers.
     */
    public Collection getHeaders() {
        return headers;
    }

    /**
     * Adds a cookie.
     */
    @Override
    public void addCookie(final Cookie cookie) {
        cookies.add(cookie);
        super.addCookie(cookie);
    }

    /**
     * Gets all the cookies.
     */
    public Collection getCookies() {
        return cookies;
    }

    /**
     * Flushes buffer and commits response to client.
     */
    @Override
    public void flushBuffer() throws IOException {
        flush();
        super.flushBuffer();
    }

    /**
     * Resets the response.
     */
    @Override
    public void reset() {
        super.reset();
        cookies.clear();
        headers.clear();
        statusCode = SC_OK;
        contentType = null;
        contentLength = 0;
    }

    /**
     * Resets the buffers.
     */
    @Override
    public void resetBuffer() {
        super.resetBuffer();
    }

    /**
     * Flushes all the streams for this response.
     */
    public void flush() throws IOException {
        if (writer != null) {
            writer.flush();
        }
        outstr.flush();
    }
}
class FilterServletOutputStream extends ServletOutputStream {

    private OutputStream stream;

    /**
     * Creates a FilterServletOutputStream.
     */
    public FilterServletOutputStream(final OutputStream stream) {
        this.stream = stream;
    }

    /**
     * Writes to the stream.
     */
    @Override
    public void write(final int b) throws IOException {
        stream.write(b);
    }

    /**
     * Writes to the stream.
     */
    @Override
    public void write(final byte[] b) throws IOException {
        stream.write(b);
    }

    /**
     * Writes to the stream.
     */
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        stream.write(b, off, len);
    }

    @Override
    public boolean isReady() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void setWriteListener(WriteListener wl) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
}
class ResponseUtil {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(ResponseUtil.class);



    /**
     * Gzipping an empty file or stream always results in a 20 byte output
     * This is in java or elsewhere.
     * <p/>
     * On a unix system to reproduce do <code>gzip -n empty_file</code>. -n tells gzip to not
     * include the file name. The resulting file size is 20 bytes.
     * <p/>
     * Therefore 20 bytes can be used indicate that the gzip byte[] will be empty when ungzipped.
     */
    private static final int EMPTY_GZIPPED_CONTENT_SIZE = 20;

    /**
     * Utility class. No public constructor.
     */
    private ResponseUtil() {
        //noop
    }


    /**
     * Checks whether a gzipped body is actually empty and should just be zero.
     * When the compressedBytes is {@link #EMPTY_GZIPPED_CONTENT_SIZE} it should be zero.
     *
     * @param compressedBytes the gzipped response body
     * @param request         the client HTTP request
     * @return true if the response should be 0, even if it is isn't.
     */
    public static boolean shouldGzippedBodyBeZero(byte[] compressedBytes, HttpServletRequest request) {

        //Check for 0 length body
        if (compressedBytes.length == EMPTY_GZIPPED_CONTENT_SIZE) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(request.getRequestURL() + " resulted in an empty response.");
            }
            return true;
        } else {
            return false;
        }
    }


    /**
     * Performs a number of checks to ensure response saneness according to the rules of RFC2616:
     * <ol>
     * <li>If the response code is {@link javax.servlet.http.HttpServletResponse#SC_NO_CONTENT} then it is illegal for the body
     * to contain anything. See http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.5
     * <li>If the response code is {@link javax.servlet.http.HttpServletResponse#SC_NOT_MODIFIED} then it is illegal for the body
     * to contain anything. See http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.5
     * </ol>
     *
     * @param request         the client HTTP request
     * @param responseStatus         the responseStatus
     * @return true if the response should be 0, even if it is isn't.
     */
    public static boolean shouldBodyBeZero(HttpServletRequest request, int responseStatus) {

        //Check for NO_CONTENT
        if (responseStatus == HttpServletResponse.SC_NO_CONTENT) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(request.getRequestURL() + " resulted in a " + HttpServletResponse.SC_NO_CONTENT
                        + " response. Removing message body in accordance with RFC2616.");
            }
            return true;
        }

        //Check for NOT_MODIFIED
        if (responseStatus == HttpServletResponse.SC_NOT_MODIFIED) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(request.getRequestURL() + " resulted in a " + HttpServletResponse.SC_NOT_MODIFIED
                        + " response. Removing message body in accordance with RFC2616.");
            }
            return true;
        }

        return false;
    }

    /**
     * Adds the gzip HTTP header to the response. This is need when a gzipped body is returned so that browsers can properly decompress it.
     * <p/>
     * @param response the response which will have a header added to it. I.e this method changes its parameter
     * @throws ResponseHeadersNotModifiableException Either the response is committed or we were called using the include method
     * from a {@link javax.servlet.RequestDispatcher#include(javax.servlet.ServletRequest, javax.servlet.ServletResponse)}
     * method and the set set header is ignored.
     */
    public static void addGzipHeader(final HttpServletResponse response) {
        response.setHeader("Content-Encoding", "gzip");
        boolean containsEncoding = response.containsHeader("Content-Encoding");
        if (!containsEncoding) {
            throw new RuntimeException("Failure when attempting to set "
                    + "Content-Encoding: gzip");
        }
    }


}
abstract class Filter implements javax.servlet.Filter {
    /**
     * If a request attribute NO_FILTER is set, then filtering will be skipped
     */
    public static final String NO_FILTER = "NO_FILTER";
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(Filter.class);

    /**
     * The filter configuration.
     */
    protected FilterConfig filterConfig;

    /**
     * The exceptions to log differently, as a comma separated list
     */
    protected String exceptionsToLogDifferently;


    /**
     * A the level of the exceptions which will be logged differently
     */
    protected String exceptionsToLogDifferentlyLevel;


    /**
     * Most {@link Throwable}s in Web applications propagate to the user. Usually they are logged where they first
     * happened. Printing the stack trace once a {@link Throwable} as propagated to the servlet is sometimes
     * just clutters the log.
     * <p/>
     * This field corresponds to an init-param of the same name. If set to true stack traces will be suppressed.
     */
    protected boolean suppressStackTraces;


    /**
     * Performs the filtering.  This method calls template method
     * {@link #doFilter(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain) } which does the filtering.
     * This method takes care of error reporting and handling.
     * Errors are reported at {@link Log#warn(Object)} level because http tends to produce lots of errors.
     */
    @Override
    public final void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response;
        try {
            //NO_FILTER set for RequestDispatcher forwards to avoid double gzipping
            if (filterNotDisabled(httpRequest)) {
                doFilter(httpRequest, httpResponse, chain);
            } else {
                chain.doFilter(request, response);
            }
            // Flush the response
            String implementationVendor = request.getClass().getPackage().getImplementationVendor();
            if (implementationVendor != null && implementationVendor.equals("\"Evermind\"")) {
                httpResponse.getOutputStream().flush();
            } else {
                httpResponse.flushBuffer();
            }

        } catch (final Throwable throwable) {
            LOG.info("增加zip压缩失败："+httpRequest.getRequestURI());
        }
    }

    /**
     * Filters can be disabled programmatically by adding a {@link #NO_FILTER} parameter to the request.
     * This parameter is normally added to make RequestDispatcher include and forwards work.
     *
     * @param httpRequest the request
     * @return true if NO_FILTER is not set.
     */
    protected boolean filterNotDisabled(final HttpServletRequest httpRequest) {
        return httpRequest.getAttribute(NO_FILTER) == null;
    }

    /**
     * Checks whether a throwable, its root cause if it is a {@link ServletException}, or its cause, if it is a
     * Chained Exception matches an entry in the exceptionsToLogDifferently list
     *
     * @param throwable
     * @return true if the class name of any of the throwables is found in the exceptions to log differently
     */
    private boolean matches(Throwable throwable) {
        if (exceptionsToLogDifferently == null) {
            return false;
        }
        if (exceptionsToLogDifferently.indexOf(throwable.getClass().getName()) != -1) {
            return true;
        }
        if (throwable instanceof ServletException) {
            Throwable rootCause = (((ServletException) throwable).getRootCause());
            if (exceptionsToLogDifferently.indexOf(rootCause.getClass().getName()) != -1) {
                return true;
            }
        }
        if (throwable.getCause() != null) {
            Throwable cause = throwable.getCause();
            if (exceptionsToLogDifferently.indexOf(cause.getClass().getName()) != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Initialises the filter.  Calls template method {@link #doInit()} to perform any filter specific initialisation.
     */
    @Override
    public final void init(final FilterConfig config) throws ServletException {
        try {

            this.filterConfig = config;
            processInitParams(config);


            // Attempt to initialise this filter
            doInit();
        } catch (final Exception e) {
            LOG.error("Could not initialise servlet filter.", e);
            throw new ServletException("Could not initialise servlet filter.", e);
        }
    }

    private void processInitParams(final FilterConfig config) throws ServletException {
        String exceptions = config.getInitParameter("exceptionsToLogDifferently");
        String level = config.getInitParameter("exceptionsToLogDifferentlyLevel");
        String suppressStackTracesString = config.getInitParameter("suppressStackTraces");
        suppressStackTraces = Boolean.valueOf(suppressStackTracesString).booleanValue();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Suppression of stack traces enabled for " + this.getClass().getName());
        }

        if (exceptions != null) {
            validateMandatoryParameters(exceptions, level);
            validateLevel(level);
            exceptionsToLogDifferentlyLevel = level;
            exceptionsToLogDifferently = exceptions;
            if (LOG.isDebugEnabled()) {
                LOG.debug("Different logging levels configured for " + this.getClass().getName());
            }
        }
    }

    private void validateMandatoryParameters(String exceptions, String level) throws ServletException {
        if ((exceptions != null && level == null) || (level != null && exceptions == null)) {
            throw new ServletException("Invalid init-params. Both exceptionsToLogDifferently"
                    + " and exceptionsToLogDifferentlyLevelvalue should be specified if one is"
                    + " specified.");
        }
    }

    private void validateLevel(String level) throws ServletException {
        //Check correct level set
        if (!(level.equals("debug")
                || level.equals("info")
                || level.equals("warn")
                || level.equals("error")
                || level.equals("fatal"))) {
            throw new ServletException("Invalid init-params value for \"exceptionsToLogDifferentlyLevel\"."
                    + "Must be one of debug, info, warn, error or fatal.");
        }
    }

    /**
     * Destroys the filter. Calls template method {@link #doDestroy()}  to perform any filter specific
     * destruction tasks.
     */
    @Override
    public final void destroy() {
        this.filterConfig = null;
        doDestroy();
    }

    /**
     * Checks if request accepts the named encoding.
     */
    protected boolean acceptsEncoding(final HttpServletRequest request, final String name) {
        final boolean accepts = headerContains(request, "Accept-Encoding", name);
        return accepts;
    }

    /**
     * Checks if request contains the header value.
     */
    private boolean headerContains(final HttpServletRequest request, final String header, final String value) {

        logRequestHeaders(request);

        final Enumeration accepted = request.getHeaders(header);
        while (accepted.hasMoreElements()) {
            final String headerValue = (String) accepted.nextElement();
            if (headerValue.indexOf(value) != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Logs the request headers, if debug is enabled.
     *
     * @param request
     */
    protected void logRequestHeaders(final HttpServletRequest request) {
        if (LOG.isDebugEnabled()) {
            Map headers = new HashMap();
            Enumeration enumeration = request.getHeaderNames();
            StringBuilder logLine = new StringBuilder();
            logLine.append("Request Headers");
            while (enumeration.hasMoreElements()) {
                String name = (String) enumeration.nextElement();
                String headerValue = request.getHeader(name);
                headers.put(name, headerValue);
                logLine.append(": ").append(name).append(" -> ").append(headerValue);
            }
            LOG.debug(logLine.toString());
        }
    }



    /**
     * A template method that performs any Filter specific destruction tasks.
     * Called from {@link #destroy()}
     */
    protected abstract void doDestroy();


    /**
     * A template method that performs the filtering for a request.
     * Called from {@link #doFilter(ServletRequest, ServletResponse, FilterChain)}.
     */
    protected abstract void doFilter(final HttpServletRequest httpRequest, final HttpServletResponse httpResponse,
                                     final FilterChain chain) throws Throwable;

    /**
     * A template method that performs any Filter specific initialisation tasks.
     * Called from {@link #init(FilterConfig)}.
     */
    protected abstract void doInit() throws Exception;

    /**
     * Returns the filter config.
     */
    public FilterConfig getFilterConfig() {
        return filterConfig;
    }

    /**
     * Determine whether the user agent accepts GZIP encoding. This feature is part of HTTP1.1.
     * If a browser accepts GZIP encoding it will advertise this by including in its HTTP header:
     * <p/>
     * <code>
     * Accept-Encoding: gzip
     * </code>
     * <p/>
     * Requests which do not accept GZIP encoding fall into the following categories:
     * <ul>
     * <li>Old browsers, notably IE 5 on Macintosh.
     * <li>Search robots such as yahoo. While there are quite a few bots, they only hit individual
     * pages once or twice a day. Note that Googlebot as of August 2004 now accepts GZIP.
     * <li>Internet Explorer through a proxy. By default HTTP1.1 is enabled but disabled when going
     * through a proxy. 90% of non gzip requests are caused by this.
     * <li>Site monitoring tools
     * </ul>
     * As of September 2004, about 34% of requests coming from the Internet did not accept GZIP encoding.
     *
     * @param request
     * @return true, if the User Agent request accepts GZIP encoding
     */
    protected boolean acceptsGzipEncoding(HttpServletRequest request) {
        return acceptsEncoding(request, "gzip");
    }

}