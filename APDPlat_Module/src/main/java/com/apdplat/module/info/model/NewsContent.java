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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Entity
@Scope("prototype")
@Component
@Searchable
public class NewsContent extends Model{
    @ManyToOne
    @ModelAttr("新闻")
    protected News news;
    
    @Enumerated(EnumType.STRING) 
    protected Lang lang;
    
    @SearchableProperty
    @ModelAttr("标题")
    protected String title;
    @Lob
    @SearchableProperty
    @ModelAttr("内容")
    protected String content;

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }    

    public Lang getLang() {
        return lang;
    }

    public void setLang(Lang lang) {
        this.lang = lang;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    
    @Override
    public String getMetaData() {
        return "新闻多语言内容";
    }
    public static void main(String[] args){
        NewsContent obj=new NewsContent();
        //生成Action
        ActionGenerator.generate(obj.getClass());
    }
}