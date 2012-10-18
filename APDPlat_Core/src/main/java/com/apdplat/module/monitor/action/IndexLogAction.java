package com.apdplat.module.monitor.action;

import com.apdplat.module.monitor.model.IndexLog;
import com.apdplat.module.monitor.service.IndexLogChartDataService;
import com.apdplat.module.monitor.service.IndexLogSingleService;
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
public class IndexLogAction extends ExtJSSimpleAction<IndexLog> {
    private String category;
    @Resource(name="indexLogSingleService")
    private IndexLogSingleService indexLogSingleService;
    @Override
    public String query(){
        LogQueue.getLogQueue().saveLog();
        return super.query();
    }
    @Override
    protected void afterRender(Map map,IndexLog obj){
        map.put("processTime", obj.getProcessTimeStr());
        map.remove("updateTime");
        map.remove("createTime");
        map.remove("appName");
    }
    @Override
    protected String generateReportData(List<IndexLog> models) {
        LinkedHashMap<String,Long> data=new LinkedHashMap<>();
        if("rate".equals(category)){
            data=IndexLogChartDataService.getRateData(models);
        }
        if("sequence".equals(category)){
            data=IndexLogChartDataService.getSequenceData(models);
        }
        
        return indexLogSingleService.getXML(data);
    }
    public void setCategory(String category) {
        this.category = category;
    }
}
