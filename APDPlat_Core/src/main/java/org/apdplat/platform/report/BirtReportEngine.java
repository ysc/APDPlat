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

package org.apdplat.platform.report;

import javax.servlet.ServletContext;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.apdplat.platform.util.ConvertUtils;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.IPlatformContext;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.core.framework.PlatformServletContext;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineConstants;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;

/**
 *
 * @author 杨尚川
 */
public class BirtReportEngine {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(BirtReportEngine.class);
    private static IReportEngine reportEngine = null;

    private BirtReportEngine(){}

    public static synchronized IReportEngine getBirtEngine(ServletContext sc) {
        if (reportEngine == null) {
            LOG.info("开始初始化报表引擎");
            long start=System.currentTimeMillis();
            float total=(float)Runtime.getRuntime().totalMemory()/1000000;
            EngineConfig config = new EngineConfig();

            config.getAppContext().put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY, BirtReportEngine.class.getClassLoader());
            config.setEngineHome("");

            IPlatformContext context = new PlatformServletContext(sc);
            config.setPlatformContext(context);

            try {
                Platform.startup(config);
            } catch (BirtException e) {
                LOG.error("BIRT启动失败",e);
            }

            IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
            reportEngine = factory.createReportEngine(config);
            total=(float)Runtime.getRuntime().totalMemory()/1000000 - total;
            LOG.info("完成初始化报表引擎，耗时："+ConvertUtils.getTimeDes(System.currentTimeMillis()-start)+" ,耗费内存："+total+"M");
        }
        return reportEngine;
    }
}