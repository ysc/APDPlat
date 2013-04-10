/**
 * 
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川
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

package com.apdplat.module.info.model;

import com.apdplat.platform.annotation.ModelAttr;
import com.apdplat.platform.annotation.ModelAttrRef;
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
import javax.persistence.Transient;
import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableComponent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Entity
@Scope("prototype")
@Component
@Searchable
public class News extends Model{
    @Transient
    @ModelAttr("语言")
    protected String lang="zh";
    
    @ManyToOne
    @RenderIgnore
    @SearchableComponent
    @ModelAttr("类型")
    @ModelAttrRef("infoTypeName")
    protected InfoType infoType;
    
    @ModelAttr("是否可用")
    protected boolean enabled=true;
    
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "news")
    @RenderIgnore
    @SearchableComponent
    @ModelAttr("多语言内容")
    protected List<NewsContent> newsContents=new ArrayList<>();
    
    public void forceSpecifyLanguageForCreate(String language){
        if(newsContents.size()==1 && id==null){
            newsContents.get(0).setLang(Lang.valueOf(language));
        }        
    }
    
    public String getTitle(){
        for(NewsContent newsContent : newsContents){
            if(newsContent.getLang().getSymbol().equals(lang)){
                return newsContent.getTitle();
            }
        }
        return null;
    }
    //setTitle方法依赖于setLang方法先执行
    public void setTitle(String title){
        log.info("设置标题");
        log.info("模型语言："+lang);
        NewsContent newsContent = getNewsContent();
        newsContent.setTitle(title);
    }
    
    public String getContent(){
        for(NewsContent newsContent : newsContents){
            if(newsContent.getLang().getSymbol().equals(lang)){
                return newsContent.getContent();
            }
        }
        return null;
    }
    //setContent方法依赖于setLang方法先执行
    public void setContent(String content){
        log.info("设置内容");
        log.info("模型语言："+lang);
        NewsContent newsContent = getNewsContent();
        newsContent.setContent(content);
    }
    private NewsContent getNewsContent(){
        for(NewsContent newsContent : newsContents){
            if(newsContent.getLang().getSymbol().equals(lang)){
                return newsContent;
            }
        }
        NewsContent newsContent = new NewsContent();
        newsContent.setLang(Lang.valueOf(lang));
        newsContent.setNews(this);
        newsContents.add(newsContent);
        return newsContent;
    }
    
    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
    public InfoType getInfoType() {
        return infoType;
    }

    public void setInfoType(InfoType infoType) {
        this.infoType = infoType;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }    

    public void addNewsContent(NewsContent newsContent) {
        this.newsContents.add(newsContent);
    }

    public void removeNewsContent(NewsContent newsContent) {
        this.newsContents.remove(newsContent);
    }

    public void clear(){
        this.newsContents.clear();
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