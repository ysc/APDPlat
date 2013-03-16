package com.apdplat.module.info.action;

import com.apdplat.module.info.model.InfoType;
import com.apdplat.module.info.model.News;
import com.apdplat.module.info.service.InfoTypeService;
import com.apdplat.platform.action.ExtJSSimpleAction;
import com.apdplat.platform.action.converter.DateTypeConverter;
import com.apdplat.platform.criteria.Operator;
import com.apdplat.platform.criteria.Property;
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
    private String lang = "zh";
    private int infoTypeId;
    
    //setTitle和setContent依赖于setLang，所以在创建的时候没有办法确保顺序
    //所以要强制指定
    @Override
    protected void assemblyModelForCreate(News model) {
        model.forceSpecifyLanguageForCreate(lang);
    }
    @Override
    protected void assemblyModelForPartUpdate(List<Property> properties) {
        for(Property property : properties){
            if("lang".equals(property.getName())){
                properties.remove(property);
            }
        }
    }
    @Override
    protected void old(News model) {
        log.info("控制器设置语言："+lang);
        model.setLang(lang);
    }
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
        model.setLang(lang);
        render(map,model);
        map.put("infoTypeId", model.getInfoType().getId());
        map.put("content", model.getContent());
    }
    @Override
    protected void renderJsonForQuery(List result) {
        for (News news : page.getModels()) {
            //重新刷新，否则取不到News.newsContents，因为使用了延迟加载
            news=service.retrieve(modelClass, news.getId());
            Map temp = new HashMap();
            render(temp,news);

            result.add(temp);
        }
    }
    @Override
    protected void render(Map map,News model){
        model.setLang(lang);
        map.put("id", model.getId());
        map.put("version", model.getVersion());
        map.put("title", model.getTitle());
        map.put("username", model.getOwnerUser().getUsername());
        map.put("orgname", model.getOwnerUser().getOrg().getOrgName());
        model.getInfoType().setLang(lang);
        map.put("infoTypeName", model.getInfoType().getInfoTypeName());
        map.put("createTime", DateTypeConverter.toDefaultDateTime(model.getCreateTime()));
        map.put("updateTime", DateTypeConverter.toDefaultDateTime(model.getUpdateTime()));
        map.put("enabled", model.isEnabled()==true?"是":"否");
    }
    public void setInfoTypeId(int infoTypeId) {
        this.infoTypeId = infoTypeId;
    }
    public void setLang(String lang) {
        this.lang = lang;
    }
}
