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

package org.apdplat.module.monitor.action;

import org.apdplat.module.monitor.model.ProcessTime;
import org.apdplat.module.monitor.service.ProcessTimeChartDataService;
import org.apdplat.module.monitor.service.ProcessTimeSingleService;
import org.apdplat.platform.action.ExtJSSimpleAction;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.struts2.convention.annotation.Namespace;
import org.apdplat.platform.log.BufferLogCollector;
import org.apdplat.platform.service.ServiceFacade;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Scope("prototype")
@Controller
@Namespace("/monitor")
public class ProcessTimeAction extends ExtJSSimpleAction<ProcessTime> {
    private String category;
    private int top;
    @Resource(name="processTimeSingleService")
    private ProcessTimeSingleService processTimeSingleService;
    //使用日志数据库
    @Resource(name = "serviceFacadeForLog")
    private ServiceFacade service;
    
    @Override
    public ServiceFacade getService(){
        return service;
    }
    @Override
    public String query(){
        BufferLogCollector.handleLog();
        return super.query();
    }
    @Override
    protected void afterRender(Map map,ProcessTime obj){
        map.put("processTime", obj.getProcessTimeStr());
        map.remove("updateTime");
        map.remove("createTime");
        map.remove("appName");
    }
    @Override
    protected String generateReportData(List<ProcessTime> models) {
        LinkedHashMap<String,Long> data=new LinkedHashMap<>();
        if("userTime".equals(category)){
            data=ProcessTimeChartDataService.getUserTimeData(models);
        
            //需要排序
            return processTimeSingleService.getXML(data,true);
        }
        if("top".equals(category)){
            data=ProcessTimeChartDataService.getTopData(models,top);
            
            //需要排序
            return processTimeSingleService.getXML(data,true);
        }
        if("sequenceSS".equals(category)){
            data=ProcessTimeChartDataService.getSequenceDataSS(models);
        }
        if("sequenceMM".equals(category)){
            data=ProcessTimeChartDataService.getSequenceDataMM(models);
        }
        if("sequenceHH".equals(category)){
            data=ProcessTimeChartDataService.getSequenceDataHH(models);
        }
        if("sequenceDD".equals(category)){
            data=ProcessTimeChartDataService.getSequenceDataDD(models);
        }
        if("sequenceMonth".equals(category)){
            data=ProcessTimeChartDataService.getSequenceDataMonth(models);
        }
        if("processRate".equals(category)){
            data=ProcessTimeChartDataService.getProcessRate(models);
        }
        //不能排序
        return processTimeSingleService.getXML(data,false);
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public void setTop(int top) {
        this.top = top;
    }
}