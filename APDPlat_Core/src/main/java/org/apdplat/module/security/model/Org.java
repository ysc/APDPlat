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

package org.apdplat.module.security.model;

import org.apdplat.platform.annotation.ModelAttr;
import org.apdplat.platform.annotation.ModelAttrRef;
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
import org.apdplat.platform.search.annotations.SearchableProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Entity
@Scope("prototype")
@Component
@XmlRootElement
@XmlType(name = "Org")
@Searchable
@Database
public class Org extends SimpleModel{
    @SearchableProperty
    @ModelAttr("组织架构名称")
    protected String orgName;
    @SearchableProperty
    @ModelAttr("负责人姓名")
    protected String chargeMan;
    @SearchableProperty
    @ModelAttr("联系电话")
    protected String phone;
    @SearchableProperty
    @ModelAttr("办公地址")
    protected String address;
    @SearchableProperty
    @ModelAttr("部门主要职能")
    protected String functions;
    @ManyToOne
    @SearchableComponent
    @ModelAttr("上级组织架构")
    @ModelAttrRef("orgName")
    protected Org parent;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "parent")
    @OrderBy("id DESC")
    @RenderIgnore
    protected List<Org> child=new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "org")
    @OrderBy("id DESC")
    @RenderIgnore
    protected List<User> users=new ArrayList<>();

    @XmlElementWrapper(name = "subOrgs")
    @XmlElement(name = "org")
    public List<Org> getChild() {
        return child;
    }

    public void setChild(List<Org> child) {
        this.child = child;
    }

    @XmlTransient
    public Org getParent() {
        return parent;
    }

    public void setParent(Org parent) {
        this.parent = parent;
    }

    @XmlAttribute
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @XmlAttribute
    public String getChargeMan() {
        return chargeMan;
    }

    public void setChargeMan(String chargeMan) {
        this.chargeMan = chargeMan;
    }

    @XmlAttribute
    public String getFunctions() {
        return functions;
    }

    public void setFunctions(String functions) {
        this.functions = functions;
    }

    @XmlAttribute
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @XmlAttribute
    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @XmlTransient
    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public String getMetaData() {
        return "组织架构";
    }

    public static void main(String[] args){
        Org obj=new Org();
        //生成Action
        ActionGenerator.generate(obj.getClass());
    }
}