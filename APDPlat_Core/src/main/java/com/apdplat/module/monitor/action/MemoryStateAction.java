package com.apdplat.module.monitor.action;

import com.apdplat.module.monitor.model.MemoryState;
import com.apdplat.module.monitor.service.MemoryStateCategoryService;
import com.apdplat.module.monitor.service.MemoryStateChartDataService;
import com.apdplat.module.system.service.LogQueue;
import com.apdplat.platform.action.ExtJSSimpleAction;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Scope("prototype")
@Controller
@Namespace("/monitor")
public class MemoryStateAction extends ExtJSSimpleAction<MemoryState> {
    private String category;
    @Resource(name="memoryStateCategoryService")
    private MemoryStateCategoryService memoryStateCategoryService;
    @Override
    public String query(){
        LogQueue.getLogQueue().saveLog();
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
