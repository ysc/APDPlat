/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.apdplat.module.info.model;

import com.apdplat.platform.annotation.ModelAttr;
import com.apdplat.platform.annotation.RenderIgnore;
import com.apdplat.platform.generator.ActionGenerator;
import com.apdplat.platform.model.Model;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableComponent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Entity
@Scope("prototype")
@Component
@XmlRootElement
@XmlType(name = "InfoType")
@Searchable
public class InfoType extends Model{
    
    @Transient
    @ModelAttr("语言")
    protected String lang="zh";
    @ModelAttr("顺序号")
    protected int orderNum;
    @ManyToOne
    @SearchableComponent
    @ModelAttr("父类别")
    protected InfoType parent;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "parent")
    @OrderBy("orderNum ASC")
    @RenderIgnore
    @ModelAttr("子类别")
    protected List<InfoType> child=new ArrayList<>();
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "infoType")
    @RenderIgnore
    @ModelAttr("多语言内容")
    protected List<InfoTypeContent> infoTypeContents=new ArrayList<>();    

    public void forceSpecifyLanguageForCreate(String language){
        if(infoTypeContents.size()==1 && id==null){
            infoTypeContents.get(0).setLang(Lang.valueOf(language));
        }        
    }
    
    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
    
    public String getInfoTypeName(){
        for(InfoTypeContent infoTypeContent : infoTypeContents){
            if(infoTypeContent.getLang().getSymbol().equals(lang)){
                return infoTypeContent.getInfoTypeName();
            }
        }
        return null;
    }
    //setInfoTypeName方法依赖于setLang方法先执行
    public void setInfoTypeName(String infoTypeName){
        log.info("设置新闻类别名称");
        log.info("模型语言："+lang);
        InfoTypeContent infoTypeContent = getInfoTypeContent();
        infoTypeContent.setInfoTypeName(infoTypeName);
    }
    private InfoTypeContent getInfoTypeContent(){
        for(InfoTypeContent infoTypeContent : infoTypeContents){
            if(infoTypeContent.getLang().getSymbol().equals(lang)){
                return infoTypeContent;
            }
        }
        InfoTypeContent infoTypeContent = new InfoTypeContent();
        infoTypeContent.setLang(Lang.valueOf(lang));
        infoTypeContent.setInfoType(this);
        infoTypeContents.add(infoTypeContent);
        return infoTypeContent;
    }

    @XmlElementWrapper(name = "subInfoTypes")
    @XmlElement(name = "infoType")
    public List<InfoType> getChild() {
        return child;
    }

    public void setChild(List<InfoType> child) {
        this.child = child;
    }

    @XmlTransient
    public InfoType getParent() {
        return parent;
    }

    public void setParent(InfoType parent) {
        this.parent = parent;
    }

    @XmlElementWrapper(name = "infoTypeContents")
    @XmlElement(name = "infoTypeContent")
    public List<InfoTypeContent> getInfoTypeContents() {
        return infoTypeContents;
    }

    public void addInfoTypeContent(InfoTypeContent infoTypeContent) {
        this.infoTypeContents.add(infoTypeContent);
    }

    public void removeInfoTypeContent(InfoTypeContent infoTypeContent) {
        this.infoTypeContents.remove(infoTypeContent);
    }


    @XmlAttribute
    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    @Override
    public String getMetaData() {
        return "新闻类别";
    }
    public static void main(String[] args){
        InfoType obj=new InfoType();
        //生成Action
        ActionGenerator.generate(obj.getClass());
    }
}