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

package org.apdplat.module.module.model;

import org.apdplat.module.module.service.ModuleService;
import org.apdplat.platform.annotation.ModelAttr;
import org.apdplat.platform.annotation.ModelAttrRef;
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
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
/**
 *模块对象
 * @author 杨尚川
 */
@Entity
@Scope("prototype")
@Component
@XmlRootElement
@XmlType(name = "Module")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE) 
@Database
public class Module extends SimpleModel {

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "parentModule")
    @OrderBy("orderNum ASC")
    //不缓存，如果缓存则在修改排序号之后数据不会失效
    //@Cache(usage = CacheConcurrencyStrategy.READ_WRITE) 
    protected List<Module> subModules = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "module")
    @OrderBy("orderNum ASC")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE) 
    protected List<Command> commands = new ArrayList<>();
    @ManyToOne
    @ModelAttr("父模块")
    @ModelAttrRef("chinese")
    protected Module parentModule;
    @ModelAttr("模块英文名称")
    protected String english;
    @ModelAttr("模块中文名称")
    protected String chinese;
    @ModelAttr("排序号")
    protected int orderNum;
    @ModelAttr("是否显示")
    protected boolean display=true;
    @ModelAttr("链接地址")
    protected String url;

    @XmlElementWrapper(name = "subModules")
    @XmlElement(name = "module")
    public List<Module> getSubModules() {
        return subModules;
    }

    public void addSubModule(Module subModule) {
        this.subModules.add(subModule);
    }

    public void removeSubModule(Module subModule) {
        this.subModules.remove(subModule);
    }
    
    @XmlAttribute
    public String getUrl() {
        String result="";
        if(url==null){
            result="../platform/"+ ModuleService.getModulePath(this.getParentModule())+this.getEnglish()+".jsp";
        }else{
            result=url;
        }
        return result;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @XmlElementWrapper(name = "commands")
    @XmlElement(name = "command")
    public List<Command> getCommands() {
        return commands;
    }

    public void addCommand(Command command) {
        this.commands.add(command);
    }

    public void removeCommand(Command command) {
        this.commands.remove(command);
    }

    @XmlTransient
    public Module getParentModule() {
        return parentModule;
    }

    public void setParentModule(Module parentModule) {
        this.parentModule = parentModule;
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

    @XmlAttribute
    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    @XmlAttribute
    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Module other = (Module) obj;
        if ((this.chinese == null) ? (other.chinese != null) : !this.chinese.equals(other.chinese)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.english != null ? this.english.hashCode() : 0);
        return hash;
    }

    @Override
    public String getMetaData() {
        return "模块信息";
    }
}