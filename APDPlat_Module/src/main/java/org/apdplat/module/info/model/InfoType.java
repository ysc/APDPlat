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

package org.apdplat.module.info.model;

import org.apdplat.platform.annotation.ModelAttr;
import org.apdplat.platform.annotation.RenderIgnore;
import org.apdplat.platform.generator.ActionGenerator;
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
import org.apdplat.platform.annotation.Database;
import org.apdplat.platform.model.SimpleModel;
import org.apdplat.platform.search.annotations.Searchable;
import org.apdplat.platform.search.annotations.SearchableComponent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Entity
@Scope("prototype")
@Component
@XmlRootElement
@XmlType(name = "InfoType")
@Searchable
@Database
public class InfoType extends SimpleModel{
    
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
        LOG.info("设置新闻类别名称");
        LOG.info("模型语言："+lang);
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