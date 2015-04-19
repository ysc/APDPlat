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
import org.apdplat.platform.generator.ActionGenerator;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.apdplat.platform.annotation.Database;
import org.apdplat.platform.model.SimpleModel;
import org.apdplat.platform.search.annotations.Searchable;
import org.apdplat.platform.search.annotations.SearchableProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Entity
@Scope("prototype")
@Component
@XmlType(name = "InfoTypeContent")
@Searchable
@Database
public class InfoTypeContent extends SimpleModel{
    @ManyToOne
    @ModelAttr("新闻类别")
    protected InfoType infoType;    
    @ModelAttr("多国语言")
    @Enumerated(EnumType.STRING) 
    protected Lang lang;
    
    @SearchableProperty
    @ModelAttr("类别名称")
    protected String infoTypeName;


    @XmlAttribute
    public Lang getLang() {
        return lang;
    }

    public void setLang(Lang lang) {
        this.lang = lang;
    }

    @XmlTransient
    public InfoType getInfoType() {
        return infoType;
    }

    public void setInfoType(InfoType infoType) {
        this.infoType = infoType;
    }

    @XmlAttribute
    public String getInfoTypeName() {
        return infoTypeName;
    }

    public void setInfoTypeName(String infoTypeName) {
        this.infoTypeName = infoTypeName;
    }


    
    @Override
    public String getMetaData() {
        return "新闻类别多语言内容";
    }
    public static void main(String[] args){
        InfoTypeContent obj=new InfoTypeContent();
        //生成Action
        ActionGenerator.generate(obj.getClass());
    }
}