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

package org.apdplat.module.security.service;

import java.io.ByteArrayOutputStream;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apdplat.module.system.service.SystemListener;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.apdplat.platform.report.BirtReportEngine;
import org.apdplat.platform.util.ConvertUtils;
import org.apdplat.platform.util.FileUtils;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.HTMLServerImageHandler;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.springframework.stereotype.Service;

/**
 *
 * @author 杨尚川
 */
@Service
public class UserReportService {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(UserReportService.class);
    private IReportEngine birtReportEngine = null;
    private static  String reportPath="/platform/reports/security/user.rptdesign";

    public byte[] getReport(ServletContext sc, HttpServletRequest req) {
        LOG.info("开始渲染报表");
        long start=System.currentTimeMillis();
        float total=(float)Runtime.getRuntime().totalMemory()/1000000;
        
        this.birtReportEngine = BirtReportEngine.getBirtEngine(sc);
        IReportRunnable design;
        try {
            LOG.info("report path:"+reportPath);
            reportPath=FileUtils.getAbsolutePath(reportPath);
            LOG.info("report path:"+reportPath);
            design = birtReportEngine.openReportDesign(reportPath);
            IRunAndRenderTask task = birtReportEngine.createRunAndRenderTask(design);

            task.getAppContext().put("BIRT_VIEWER_HTTPSERVLET_REQUEST", req );
            task.setParameterValue("title", "用户图形报表");
            task.setParameterValue("tip", "测试用户报表");
            
            HTMLRenderOption options = new HTMLRenderOption();
            options.setOutputFormat(HTMLRenderOption.OUTPUT_FORMAT_HTML);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            options.setOutputStream(out);
            options.setImageHandler(new HTMLServerImageHandler());
            options.setBaseImageURL(SystemListener.getContextPath() + "/platform/reports/images");
            options.setImageDirectory(FileUtils.getAbsolutePath("/platform/reports/images"));
            task.setRenderOption(options);

            task.run();
            task.close();
            total=(float)Runtime.getRuntime().totalMemory()/1000000 - total;
            LOG.info("完成渲染报表，耗时："+ConvertUtils.getTimeDes(System.currentTimeMillis()-start)+" ,耗费内存："+total+"M");
            return out.toByteArray();
        } catch (EngineException | NumberFormatException e) {
            LOG.error("输出报表出错",e);
        }
        return null;
    }
}