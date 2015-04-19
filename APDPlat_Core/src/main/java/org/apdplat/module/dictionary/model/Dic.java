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

package org.apdplat.module.dictionary.model;

import org.apdplat.platform.annotation.ModelAttr;
import org.apdplat.platform.annotation.ModelAttrRef;
import org.apdplat.platform.generator.ActionGenerator;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.apdplat.platform.annotation.Database;
import org.apdplat.platform.model.SimpleModel;
import org.apdplat.platform.search.annotations.Searchable;
import org.apdplat.platform.search.annotations.SearchableProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
/**
 *数据字典
 * @author 杨尚川
 */
@Entity
@Scope("prototype")
@Component
@XmlRootElement
@XmlType(name = "Dic")
@Searchable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE) 
@Database
public class Dic extends SimpleModel {

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "parentDic")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE) 
    protected List<Dic> subDics = new ArrayList<>();
    //不对字典项集合进行缓存，因为当字典项改变导致缓存失效后，字典的缓存没有失效
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "dic")
    @OrderBy("orderNum ASC")
    protected List<DicItem> dicItems = new ArrayList<>();
    @ManyToOne
    @ModelAttr("父字典")
    @ModelAttrRef("chinese")
    protected Dic parentDic;
    @ModelAttr("英文名称")
    protected String english;
    @SearchableProperty
    @ModelAttr("中文名称")
    protected String chinese;
    //当此属性为真时，此词典和模型没有建立关联关系，而只是把此词典的编码值作为字符串赋值给模型
    @ModelAttr("伪词典")
    protected boolean justCode;

    @XmlAttribute
    public boolean isJustCode() {
        return justCode;
    }

    public void setJustCode(boolean justCode) {
        this.justCode = justCode;
    }

    
    @XmlAttribute
    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    @XmlAttribute
    public String getChinese() {
        return chinese;
    }

    public void setChinese(String chinese) {
        this.chinese = chinese;
    }

    @XmlTransient
    public Dic getParentDic() {
        return parentDic;
    }

    public void setParentDic(Dic parentDic) {
        this.parentDic = parentDic;
    }

    @XmlElementWrapper(name = "dicItems")
    @XmlElement(name = "dicItem")
    public List<DicItem> getDicItems() {
        return dicItems;
    }

    public void addDicItem(DicItem dicItem) {
        this.dicItems.add(dicItem);
    }

    public void removeDicItem(DicItem dicItem) {
        this.dicItems.remove(dicItem);
    }

    @XmlElementWrapper(name = "subDics")
    @XmlElement(name = "dic")
    public List<Dic> getSubDics() {
        return subDics;
    }

    public void addSubDics(Dic subDic) {
        this.subDics.add(subDic);
    }

    public void removeSubDics(Dic subDic) {
        this.subDics.remove(subDic);
    }

    @Override
    public String getMetaData() {
        return "数据字典";
    }
    public static void main(String[] args){
        Dic obj=new Dic();
        //生成Action
        ActionGenerator.generate(obj.getClass());
    }
}