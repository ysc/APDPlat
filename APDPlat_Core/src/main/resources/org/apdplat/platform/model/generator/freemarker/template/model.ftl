/**
 * 
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package ${modelInfo.modelPackage};

import org.apdplat.platform.generator.ActionGenerator;
import org.apdplat.platform.model.SimpleModel;
import org.apdplat.platform.annotation.*;
<#if modelInfo.hasDicItem>
import org.apdplat.module.dictionary.model.DicItem;
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
import org.apdplat.platform.search.annotations.*;
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
public class ${modelInfo.modelEnglish} extends SimpleModel{

<#list modelInfo.attrs as attr>
    <#if attr.renderIgnore>
    @RenderIgnore
    </#if>
    <#if attr.type == 'String' || attr.type == 'Integer' || attr.type == 'Float'>
        <#if attr.searchable>
    @SearchableProperty
        </#if>
    @ModelAttr("${attr.des}")
        <#if attr.type == 'String' && attr.length gt 0>
    @Column(length=${attr.length})
        </#if>
    protected ${attr.type} ${attr.name};
    </#if>
    <#if attr.dic == 'SimpleDic' || attr.dic == 'TreeDic'>
    @ManyToOne
        <#if attr.searchable>
    @SearchableComponent(prefix="${attr.name}_")
        </#if>
    @ModelAttr("${attr.des}")
    @${attr.dic}("${attr.dicName}")
    protected ${attr.type} ${attr.name};
    </#if>
    <#if attr.type == 'Date' || attr.type == 'Time'>
        <#if attr.searchable && attr.type == 'Date'>
    @SearchableProperty(format = "yyyy-MM-dd")
        </#if>
        <#if attr.searchable && attr.type == 'Time'>
    @SearchableProperty(format = "yyyy-MM-dd_HH:mm:ss")
        </#if>
        <#if attr.type == 'Date'>
    @RenderDate
        </#if>
        <#if attr.type == 'Time'>
    @RenderTime
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