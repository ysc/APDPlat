package ${modelInfo.modelPackage};

import com.apdplat.platform.generator.ActionGenerator;
import com.apdplat.platform.model.Model;
import com.apdplat.platform.annotation.*;
<#if modelInfo.hasDicItem>
import com.apdplat.module.dictionary.model.DicItem;
</#if>
import javax.persistence.*;
<#if modelInfo.hasDateTime>
import java.util.Date;
</#if>
<#if modelInfo.hasOneToMany>
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
</#if>
import org.compass.annotations.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
<#if modelInfo.hasMap>
import javax.xml.bind.annotation.XmlTransient;
</#if>
import javax.xml.bind.annotation.XmlType;

@Entity
@Scope("prototype")
@Component
@Searchable
@XmlRootElement
@XmlType(name = "${modelInfo.modelEnglish}")
public class ${modelInfo.modelEnglish} extends Model{

<#list modelInfo.attrs as attr>
    <#if attr.renderIgnore>
    @RenderIgnore
    </#if>
    <#if attr.type == 'String' || attr.type == 'Integer' || attr.type == 'Float'>
        <#if attr.searchable>
    @SearchableProperty
        </#if>
    @ModelAttr("${attr.des}")
    protected ${attr.type} ${attr.name};
    </#if>
    <#if attr.dic == 'SimpleDic' || attr.dic == 'TreeDic'>
    @ManyToOne
        <#if attr.searchable>
    @SearchableComponent(prefix="${attr.name}_")
        </#if>
    @ModelAttr("${attr.des}")
    @${attr.dic}("${attr.name}")
    protected ${attr.type} ${attr.name};
    </#if>
    <#if attr.type == 'Date' || attr.type == 'Time'>
        <#if attr.searchable && attr.type == 'Date'>
    @SearchableProperty(format = "yyyy-MM-dd")
        </#if>
        <#if attr.searchable && attr.type == 'Time'>
    @SearchableProperty(format = "yyyy-MM-dd_HH:mm:ss")
        </#if>
    @Temporal(TemporalType.TIMESTAMP)
    @ModelAttr("${attr.des}")
    protected Date ${attr.name};
    </#if>
    <#if attr.map == 'OneToOne'>
    @OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "${modelInfo.modelEnglish ? uncap_first}")
        <#if attr.searchable>
    @SearchableComponent
        </#if>
    @ModelAttr("${attr.des}")
    @ModelAttrRef("${attr.attrRef}")
    protected ${attr.type} ${attr.name};
    </#if>
    <#if attr.map == 'ManyToOne'>
    @ManyToOne
        <#if attr.searchable>
    @SearchableComponent
        </#if>
    @ModelAttr("${attr.des}")
    @ModelAttrRef("${attr.attrRef}")
    protected ${attr.type} ${attr.name};
    </#if>
    <#if attr.map == 'OneToMany'>
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "${modelInfo.modelEnglish ? uncap_first}")
    @OrderBy("id DESC")
        <#if attr.searchable>
    @SearchableComponent
        </#if>
    @ModelAttr("${attr.des}")
    @ModelCollRef("${attr.attrRef}")
    protected List<${attr.type}> ${attr.name}s = new ArrayList<${attr.type}>();
    </#if>

</#list>     
    
<#list modelInfo.attrs as attr>

    <#if attr.map == 'OneToMany'>
    @XmlTransient
    public List<${attr.type}> get${attr.name ? cap_first}s() {
        return Collections.unmodifiableList(this.${attr.name}s);
    }

    public void add${attr.name ? cap_first}(${attr.type} ${attr.name}) {
        this.${attr.name}s.add(${attr.name});
    }

    public void remove${attr.name ? cap_first}(${attr.type} ${attr.name}) {
        this.${attr.name}s.remove(${attr.name});
    }

    public void clear${attr.name ? cap_first}() {
        this.${attr.name}s.clear();
    }
    <#else>
        <#if attr.map == 'None'>
    @XmlAttribute
        <#else>
    @XmlTransient
        </#if>
        <#if attr.type == 'Time'>
    public Date get${attr.name ? cap_first}() {
        return ${attr.name};
    }

    public void set${attr.name ? cap_first}(Date ${attr.name}) {
        this.${attr.name} = ${attr.name};
    }
        <#else>
    public ${attr.type} get${attr.name ? cap_first}() {
        return ${attr.name};
    }

    public void set${attr.name ? cap_first}(${attr.type} ${attr.name}) {
        this.${attr.name} = ${attr.name};
    }
        </#if>
    </#if>
</#list>   
    @Override
    public String getMetaData() {
        return "${modelInfo.modelChinese}";
    }
    public static void main(String[] args){
        ${modelInfo.modelEnglish} obj=new ${modelInfo.modelEnglish}();
        //生成Action
        ActionGenerator.generate(obj.getClass());
    }
}