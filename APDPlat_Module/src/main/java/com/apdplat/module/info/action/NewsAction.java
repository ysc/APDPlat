package com.apdplat.module.info.action;

import com.apdplat.module.info.model.InfoType;
import com.apdplat.module.info.model.News;
import com.apdplat.module.info.service.InfoTypeService;
import com.apdplat.platform.action.ExtJSSimpleAction;
import com.apdplat.platform.action.converter.DateTypeConverter;
import com.apdplat.platform.criteria.Operator;
import com.apdplat.platform.criteria.PropertyCriteria;
import com.apdplat.platform.criteria.PropertyEditor;
import com.apdplat.platform.criteria.PropertyType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Scope("prototype")
@Controller
@Namespace("/info")
public class NewsAction extends ExtJSSimpleAction<News> {
    private int infoTypeId;
    //方式二：使用IN语句
    @Override
    public PropertyCriteria buildPropertyCriteria(){
        PropertyCriteria propertyCriteria=new PropertyCriteria();
        if(infoTypeId>0){
            InfoType obj=service.retrieve(InfoType.class, infoTypeId);
            //获取orgId的所有子机构的ID
            List<Integer> infoTypeIds=InfoTypeService.getChildIds(obj);
            //加上orgId
            infoTypeIds.add(obj.getId());
            
            PropertyEditor pe=new PropertyEditor("infoType.id", Operator.in, PropertyType.List, infoTypeIds);
            propertyCriteria.addPropertyEditor(pe);
            
            return propertyCriteria;
        }
         //infoTypeId==-1或infoTypeId<0代表为根节点，不加过滤条件
        return null;
    }
    @Override
    protected void renderJsonForRetrieve(Map map) {
        render(map,model);
        map.put("infoTypeId", model.getInfoType().getId());
        map.put("content", model.getContent());
    }
    @Override
    protected void renderJsonForQuery(List result) {
        for (News model : page.getModels()) {
            Map map = new HashMap();
            render(map,model);

            result.add(map);
        }
    }
    @Override
    protected void render(Map map,News model){
        map.put("id", model.getId());
        map.put("version", model.getVersion());
        map.put("title", model.getTitle());
        map.put("username", model.getOwnerUser().getUsername());
        map.put("orgname", model.getOwnerUser().getOrg().getOrgName());
        map.put("infoTypeName", model.getInfoType().getInfoTypeName());
        map.put("createTime", DateTypeConverter.toDefaultDateTime(model.getCreateTime()));
        map.put("updateTime", DateTypeConverter.toDefaultDateTime(model.getUpdateTime()));
        map.put("enabled", model.isEnabled()==true?"是":"否");
    }

    public void setInfoTypeId(int infoTypeId) {
        this.infoTypeId = infoTypeId;
    }
}
