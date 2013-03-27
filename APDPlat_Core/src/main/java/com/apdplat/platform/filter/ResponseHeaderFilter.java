/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.apdplat.platform.filter;

import com.apdplat.module.dictionary.service.DicService;
import com.apdplat.platform.log.APDPlatLogger;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Locale;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * 使用Filter来指定浏览器缓存或不缓存服务器数据
 * 此过滤器放在最前面，之后的过滤器可以覆盖该值
 * @author ysc
 */
public class ResponseHeaderFilter implements javax.servlet.Filter {
    protected static final APDPlatLogger log = new APDPlatLogger(DicService.class);

    private  String expiresSeconds;
    private  FilterConfig filterConfig;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig=filterConfig;
        expiresSeconds=filterConfig.getInitParameter("expiresSeconds");
        log.info("初始化过滤器：ResponseHeaderFilter");
        log.info("init filter：ResponseHeaderFilter", Locale.ENGLISH);
        log.info("过期秒数："+expiresSeconds);
        log.info("expires seconds："+expiresSeconds, Locale.ENGLISH);
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
        log.info("销毁过滤器：ResponseHeaderFilter");
        log.info("destroy filter：ResponseHeaderFilter", Locale.ENGLISH);
    }
}