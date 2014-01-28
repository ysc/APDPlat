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

import org.apdplat.module.monitor.model.MemoryState;
import org.apdplat.module.monitor.service.MemoryStateCategoryService;
import org.apdplat.module.monitor.service.MemoryStateChartDataService;
import org.apdplat.platform.action.ExtJSSimpleAction;
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
public class MemoryStateAction extends ExtJSSimpleAction<MemoryState> {
    private String category;
    @Resource(name="memoryStateCategoryService")
    private MemoryStateCategoryService memoryStateCategoryService;
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
    protected void afterRender(Map map,MemoryState obj){
        map.put("usingMemory", obj.getTotalMemory()-obj.getFreeMemory());
        map.remove("updateTime");
        map.remove("createTime");
        map.remove("appName");
    }
    
    @Override
    protected String generateReportData(List<MemoryState> models) {
        if("sequence".equals(category)){
            //不改变数据，就用models
        }
        if("sequenceHH".equals(category)){
            models=MemoryStateChartDataService.getSequenceDataHH(models);
        }
        if("sequenceDD".equals(category)){
            models=MemoryStateChartDataService.getSequenceDataDD(models);
        }
        if("sequenceMonth".equals(category)){
            models=MemoryStateChartDataService.getSequenceDataMonth(models);
        }
        return memoryStateCategoryService.getXML(models);
    }
    public void setCategory(String category) {
        this.category = category;
    }
}