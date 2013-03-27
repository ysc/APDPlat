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

package com.apdplat.module.monitor.action;

import com.apdplat.module.monitor.model.RuningTime;
import com.apdplat.module.monitor.service.RuningTimeChartDataService;
import com.apdplat.module.monitor.service.RuningTimeSingleService;
import com.apdplat.module.system.service.LogQueue;
import com.apdplat.platform.action.ExtJSSimpleAction;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Scope("prototype")
@Controller
@Namespace("/monitor")
public class RuningTimeAction extends ExtJSSimpleAction<RuningTime> {
    private String category;
    @Resource(name="runingTimeSingleService")
    private RuningTimeSingleService runingTimeSingleService;
    @Override
    public String query(){
        LogQueue.getLogQueue().saveLog();
        return super.query();
    }
    @Override
    protected void afterRender(Map map,RuningTime obj){
        map.put("runingTime", obj.getRuningTimeStr());
        map.remove("osName");
        map.remove("osVersion");
        map.remove("osArch");
        map.remove("jvmVersion");
        map.remove("jvmName");
        map.remove("jvmVendor");
        map.remove("updateTime");
        map.remove("createTime");
        map.remove("appName");
    }
    @Override
    protected String generateReportData(List<RuningTime> models) {
        LinkedHashMap<String,Long> data=new LinkedHashMap<>();
        if("runingRate".equals(category)){
            data=RuningTimeChartDataService.getRuningRateData(models);
        }
        if("runingSequence".equals(category)){
            data=RuningTimeChartDataService.getRuningSequence(models);
        }
        
        return runingTimeSingleService.getXML(data);
    }

    public void setCategory(String category) {
        this.category = category;
    }
}