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

package com.apdplat.module.security.service.filter;

import com.apdplat.module.security.service.SpringSecurityService;
import com.apdplat.module.security.service.UserDetailsServiceImpl;
import com.apdplat.module.security.service.UserHolder;
import com.apdplat.module.system.service.PropertyHolder;
import com.apdplat.platform.log.APDPlatLogger;
import com.apdplat.platform.util.SpringContextUtils;
import java.io.IOException;
import java.util.Collection;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author ysc
 */
public class AutoLoginFilter implements Filter {
    protected static final APDPlatLogger log = new APDPlatLogger(AutoLoginFilter.class);
    
    private UserDetailsServiceImpl userDetailsServiceImpl;
    private boolean enabled = false;
    private String defaultUserName;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        if (enabled && !UserHolder.hasLogin()) {
            if (userDetailsServiceImpl == null) {
                userDetailsServiceImpl = SpringContextUtils.getBean("userDetailsServiceImpl");
            }
            if (userDetailsServiceImpl != null) {
                UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(defaultUserName);

                UserHolder.saveUserDetailsToContext(userDetails, (HttpServletRequest) request);
                Collection<GrantedAuthority> auth=userDetails.getAuthorities();
                for(GrantedAuthority au : auth){
                    log.info("\t"+au.getAuthority());
                }
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig fc) throws ServletException {
        log.info("初始化自动登录过滤器(Initialize the automatic login filter)");
        enabled = !SpringSecurityService.isSecurity();
        defaultUserName = PropertyHolder.getProperty("auto.login.username");
        if(enabled){
            log.info("启用自动登录过滤器(Enable automatic login filter)");
        }else{            
            log.info("禁用自动登录过滤器(Disable automatic login filter)");
        }
    }

    @Override
    public void destroy() {
        log.info("销毁自动登录过滤器(Destroy the automatic login filter)");
    }
}