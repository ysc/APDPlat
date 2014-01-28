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

package org.apdplat.module.log.action;

import org.apdplat.module.log.model.OperateLog;
import org.apdplat.module.log.model.OperateStatistics;
import org.apdplat.module.log.service.OperateLogChartDataService;
import org.apdplat.module.log.service.OperateTyeCategoryService;
import org.apdplat.module.log.service.UserCategoryService;
import org.apdplat.platform.action.ExtJSSimpleAction;
import org.apdplat.platform.model.ModelMetaData;
import org.apdplat.platform.util.Struts2Utils;
import java.util.ArrayList;
import java.util.HashMap;
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
@Namespace("/log")
public class OperateLogAction extends ExtJSSimpleAction<OperateLog> {
    @Resource(name="userCategoryService")
    private UserCategoryService userCategoryService;
    @Resource(name="operateTyeCategoryService")
    private OperateTyeCategoryService operateTyeCategoryService;
    private String category;
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
    protected void afterRender(Map map,OperateLog obj){
        map.remove("updateTime");
        map.remove("createTime");
        map.remove("appName");
    }
    
    @Override
    protected String generateReportData(List<OperateLog> models) {
        List<OperateStatistics> data=OperateLogChartDataService.getData(models);
        if("user".equals(category)){
            return userCategoryService.getXML(data);
        }else{
            return operateTyeCategoryService.getXML(data);
        }
    }
    /**
     * 所有模型信息
     * @return 
     */
    public String store(){        
        List<Map<String,String>> data=new ArrayList<>();
        for(String key : ModelMetaData.getModelDes().keySet()){
            Map<String,String> temp=new HashMap<>();
            temp.put("value", ModelMetaData.getModelDes().get(key));
            temp.put("text", ModelMetaData.getModelDes().get(key));
            data.add(temp);
        }
        Struts2Utils.renderJson(data);
        return null;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}