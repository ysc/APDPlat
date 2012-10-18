package com.apdplat.module.monitor.action;

import com.apdplat.module.monitor.model.BackupLog;
import com.apdplat.module.monitor.service.BackupLogChartDataService;
import com.apdplat.module.monitor.service.BackupLogSingleService;
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
public class BackupLogAction extends ExtJSSimpleAction<BackupLog> {
    private String category;
    @Resource(name="backupLogSingleService")
    private BackupLogSingleService backupLogSingleService;
    @Override
    public String query(){
        LogQueue.getLogQueue().saveLog();
        return super.query();
    } 
    @Override
    protected void afterRender(Map map,BackupLog obj){
        map.put("processTime", obj.getProcessTimeStr());
        map.remove("updateTime");
        map.remove("createTime");
        map.remove("appName");
    }
    @Override
    protected String generateReportData(List<BackupLog> models) {
        LinkedHashMap<String,Long> data=new LinkedHashMap<>();
        if("rate".equals(category)){
            data=BackupLogChartDataService.getRateData(models);
        }
        if("sequence".equals(category)){
            data=BackupLogChartDataService.getSequenceData(models);
        }
        
        return backupLogSingleService.getXML(data);
    }
    public void setCategory(String category) {
        this.category = category;
    }
}
