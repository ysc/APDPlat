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

import org.apdplat.module.dictionary.service.DicService;
import org.apdplat.platform.log.APDPlatLogger;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Locale;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.apdplat.platform.log.APDPlatLoggerFactory;

/**
 * 使用Filter来指定浏览器缓存或不缓存服务器数据
 * 此过滤器放在最前面，之后的过滤器可以覆盖该值
 * @author 杨尚川
 */
public class ResponseHeaderFilter implements javax.servlet.Filter {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(DicService.class);

    private  String expiresSeconds;
    private  FilterConfig filterConfig;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig=filterConfig;
        expiresSeconds=filterConfig.getInitParameter("expiresSeconds");
        LOG.info("初始化过滤器：ResponseHeaderFilter");
        LOG.info("init filter：ResponseHeaderFilter", Locale.ENGLISH);
        LOG.info("过期秒数："+expiresSeconds);
        LOG.info("expires seconds："+expiresSeconds, Locale.ENGLISH);
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        for (Enumeration e = filterConfig.getInitParameterNames(); e.hasMoreElements();) {
            String headerName = (String) e.nextElement();
            if("expiresSeconds".equals(headerName)){
		httpResponse.addHeader("Expires", System.currentTimeMillis() + Integer.parseInt(expiresSeconds) * 1000 +"");
		httpResponse.addHeader("Cache-Control", "public, max-age=" + expiresSeconds);
            }else{
                httpResponse.addHeader(headerName, filterConfig.getInitParameter(headerName));
            }
        }
        
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        LOG.info("销毁过滤器：ResponseHeaderFilter");
        LOG.info("destroy filter：ResponseHeaderFilter", Locale.ENGLISH);
    }
}