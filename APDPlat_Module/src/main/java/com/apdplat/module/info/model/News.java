package com.apdplat.module.info.model;

import com.apdplat.platform.generator.ActionGenerator;
import com.apdplat.platform.annotation.ModelAttr;
import com.apdplat.platform.annotation.ModelAttrRef;
import com.apdplat.platform.annotation.RenderIgnore;
import com.apdplat.platform.model.Model;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableComponent;
import org.compass.annotations.SearchableProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Entity
@Scope("prototype")
@Component
@Searchable
public class News extends Model{
    @SearchableProperty
    @ModelAttr("标题")
    protected String title;
    @Lob
    @SearchableProperty
    @ModelAttr("内容")
    protected String content;
    @ManyToOne
    @RenderIgnore
    @SearchableComponent
    @ModelAttr("类型")
    @ModelAttrRef("infoTypeName")
    protected InfoType infoType;
    
    @ModelAttr("是否可用")
    protected boolean enabled=true;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public InfoType getInfoType() {
        return infoType;
    }

    public void setInfoType(InfoType infoType) {
        this.infoType = infoType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    
    @Override
    public String getMetaData() {
        return "新闻";
    }
    public static void main(String[] args){
        News obj=new News();
        //生成Action
        ActionGenerator.generate(obj.getClass());
    }
}
