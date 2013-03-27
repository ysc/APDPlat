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
import com.apdplat.platform.generator.ActionGenerator;
import com.apdplat.platform.model.Model;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Entity
@Scope("prototype")
@Component
@XmlType(name = "InfoTypeContent")
@Searchable
public class InfoTypeContent extends Model{
    @ManyToOne
    @ModelAttr("新闻类别")
    protected InfoType infoType;
    
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