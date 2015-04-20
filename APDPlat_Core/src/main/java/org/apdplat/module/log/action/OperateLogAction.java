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
import org.apdplat.module.log.service.OperateTypeCategoryService;
import org.apdplat.module.log.service.UserCategoryService;
import org.apdplat.platform.action.ExtJSSimpleAction;
import org.apdplat.platform.model.ModelMetaData;
import org.apdplat.platform.log.BufferLogCollector;
import org.apdplat.platform.service.ServiceFacade;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

@Scope("prototype")
@Controller
@RequestMapping("/log/operate-log/")
public class OperateLogAction extends ExtJSSimpleAction<OperateLog> {
    @Resource
    private UserCategoryService userCategoryService;
    @Resource
    private OperateTypeCategoryService operateTypeCategoryService;
    //使用日志数据库
    @Resource(name = "serviceFacadeForLog")
    private ServiceFacade service;
    
    @Override
    public ServiceFacade getService(){
        return service;
    }
    @Override
    protected  void beforeQuery(){
        BufferLogCollector.handleLog();
    }
    @Override
    protected void afterRender(Map map,OperateLog obj){
        map.remove("updateTime");
        map.remove("createTime");
        map.remove("appName");
    }
    
    @Override
    protected String generateReportData(List<OperateLog> models, String category, Integer top) {
        List<OperateStatistics> data=OperateLogChartDataService.getData(models);
        if("user".equals(category)){
            return userCategoryService.getXML(data);
        }else{
            return operateTypeCategoryService.getXML(data);
        }
    }
    /**
     * 所有模型信息
     * @return 
     */
    @ResponseBody
    @RequestMapping("store.action")
    public String store(){        
        List<Map<String,String>> map=new ArrayList<>();
        ModelMetaData.getModelDes().keySet().forEach(key -> {
            Map<String,String> temp=new HashMap<>();
            temp.put("value", ModelMetaData.getModelDes().get(key));
            temp.put("text", ModelMetaData.getModelDes().get(key));
            map.add(temp);
        });
        return toJson(map);
    }
}