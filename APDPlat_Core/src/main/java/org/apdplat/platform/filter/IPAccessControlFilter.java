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
import org.apdplat.module.security.service.filter.IPAccessControler;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.log.APDPlatLoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Locale;

/**
 * 使用Filter来执行IP访问限制策略
 * @author 杨尚川
 */
public class IPAccessControlFilter implements javax.servlet.Filter {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(DicService.class);

    private static final IPAccessControler IP_ACCESS_CONTROLER = new IPAccessControler();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOG.info("初始化过滤器：IPAccessControlFilter");
        LOG.info("init filter：IPAccessControlFilter", Locale.ENGLISH);
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        if(IP_ACCESS_CONTROLER.deny(httpRequest)){
            String message = "IP访问策略限制";
            LOG.info(message);
        }else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        LOG.info("销毁过滤器：IPAccessControlFilter");
        LOG.info("destroy filter：IPAccessControlFilter", Locale.ENGLISH);
    }
}