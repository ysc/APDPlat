package com.apdplat.module.monitor.action;

import com.apdplat.module.monitor.model.ProcessTime;
import com.apdplat.module.monitor.service.ProcessTimeChartDataService;
import com.apdplat.module.monitor.service.ProcessTimeSingleService;
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
public class ProcessTimeAction extends ExtJSSimpleAction<ProcessTime> {
    private String category;
    private int top;
    @Resource(name="processTimeSingleService")
    private ProcessTimeSingleService processTimeSingleService;
    @Override
    public String query(){
        LogQueue.getLogQueue().saveLog();
        return super.query();
    }
    @Override
    protected void afterRender(Map map,ProcessTime obj){
        map.put("processTime", obj.getProcessTimeStr());
        map.remove("updateTime");
        map.remove("createTime");
        map.remove("appName");
    }
    @Override
    protected String generateReportData(List<ProcessTime> models) {
        LinkedHashMap<String,Long> data=new LinkedHashMap<>();
        if("userTime".equals(category)){
            data=ProcessTimeChartDataService.getUserTimeData(models);
        
            //需要排序
            return processTimeSingleService.getXML(data,true);
        }
        if("top".equals(category)){
            data=ProcessTimeChartDataService.getTopData(models,top);
            
            //需要排序
            return processTimeSingleService.getXML(data,true);
        }
        if("sequenceSS".equals(category)){
            data=ProcessTimeChartDataService.getSequenceDataSS(models);
        }
        if("sequenceMM".equals(category)){
            data=ProcessTimeChartDataService.getSequenceDataMM(models);
        }
        if("sequenceHH".equals(category)){
            data=ProcessTimeChartDataService.getSequenceDataHH(models);
        }
        if("sequenceDD".equals(category)){
            data=ProcessTimeChartDataService.getSequenceDataDD(models);
        }
        if("sequenceMonth".equals(category)){
            data=ProcessTimeChartDataService.getSequenceDataMonth(models);
        }
        if("processRate".equals(category)){
            data=ProcessTimeChartDataService.getProcessRate(models);
        }
        //不能排序
        return processTimeSingleService.getXML(data,false);
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public void setTop(int top) {
        this.top = top;
    }
}
