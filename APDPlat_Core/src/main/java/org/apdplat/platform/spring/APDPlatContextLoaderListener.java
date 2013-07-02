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

package org.apdplat.platform.spring;

import javax.servlet.ServletContextEvent;
import org.apdplat.module.system.service.SystemListener;
import org.springframework.web.context.ContextLoaderListener;

/**
 * 自定义Spring的ContextLoaderListener
 * @author 杨尚川
 */
public class APDPlatContextLoaderListener extends ContextLoaderListener {
    
    @Override
    public void contextInitialized(ServletContextEvent event) {
        //接管系统的启动
        SystemListener.contextInitialized(event);
        super.contextInitialized(event);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        //接管系统的关闭
        SystemListener.contextDestroyed(event);
        super.contextDestroyed(event);
    }
}