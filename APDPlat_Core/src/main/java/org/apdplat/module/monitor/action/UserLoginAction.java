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

import org.apdplat.module.monitor.model.UserLogin;
import org.apdplat.module.monitor.service.UserLoginChartDataService;
import org.apdplat.module.monitor.service.UserLoginSingleService;
import org.apdplat.platform.action.ExtJSSimpleAction;
import org.apdplat.platform.log.BufferLogCollector;
import org.apdplat.platform.service.ServiceFacade;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Scope("prototype")
@Controller
@RequestMapping("/monitor/user-login/")
public class UserLoginAction extends ExtJSSimpleAction<UserLogin> {
    @Resource
    private UserLoginSingleService userLoginSingleService;
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
    protected void afterRender(Map map,UserLogin obj){
        map.put("onlineTime", obj.getOnlineTimeStr());
        map.remove("userAgent");
        map.remove("updateTime");
        map.remove("createTime");
        map.remove("appName");
    }
    @Override
    protected String generateReportData(List<UserLogin> models, String category, Integer top) {
        LinkedHashMap<String,Long> data=new LinkedHashMap<>();
        switch (category) {
            case "loginTimes":
                data=UserLoginChartDataService.getUserLoginTimes(models);
                break;
            case "onlineTime":
                data=UserLoginChartDataService.getUserOnlineTime(models);
                break;
        }
        
        return userLoginSingleService.getXML(data);
    }
}