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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import org.apdplat.platform.annotation.Database;
import org.apdplat.platform.model.SimpleModel;
import org.apdplat.platform.search.annotations.Searchable;
import org.apdplat.platform.search.annotations.SearchableProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Entity
@Scope("prototype")
@Component
@Searchable
@Database
public class NewsContent extends SimpleModel{
    @ManyToOne
    @ModelAttr("新闻")
    protected News news;
    
    @Enumerated(EnumType.STRING) 
    @ModelAttr("多国语言")
    protected Lang lang;
    
    @SearchableProperty
    @ModelAttr("标题")
    protected String title;
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