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
