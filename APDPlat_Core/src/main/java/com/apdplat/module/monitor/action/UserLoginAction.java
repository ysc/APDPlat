package com.apdplat.module.monitor.action;

import com.apdplat.module.monitor.model.UserLogin;
import com.apdplat.module.monitor.service.UserLoginChartDataService;
import com.apdplat.module.monitor.service.UserLoginSingleService;
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
public class UserLoginAction extends ExtJSSimpleAction<UserLogin> {
    private String category;
    @Resource(name="userLoginSingleService")
    private UserLoginSingleService userLoginSingleService;
    @Override
    public String query(){
        LogQueue.getLogQueue().saveLog();
        return super.query();
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
    protected String generateReportData(List<UserLogin> models) {
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
    public void setCategory(String category) {
        this.category = category;
    }
}
