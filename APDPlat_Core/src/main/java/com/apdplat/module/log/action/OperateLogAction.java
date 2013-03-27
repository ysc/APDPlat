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

package com.apdplat.module.log.action;

import com.apdplat.module.log.model.OperateLog;
import com.apdplat.module.log.model.OperateStatistics;
import com.apdplat.module.log.service.OperateLogChartDataService;
import com.apdplat.module.log.service.OperateTyeCategoryService;
import com.apdplat.module.log.service.UserCategoryService;
import com.apdplat.module.system.service.LogQueue;
import com.apdplat.platform.action.ExtJSSimpleAction;
import com.apdplat.platform.model.ModelMetaData;
import com.apdplat.platform.util.Struts2Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.struts2.convention.annotation.Namespace;
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
    @Override
    public String query(){
        LogQueue.getLogQueue().saveLog();
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