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
import org.apdplat.platform.annotation.ModelCollRef;
import org.apdplat.platform.generator.ActionGenerator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.apdplat.platform.annotation.Database;
import org.apdplat.platform.model.SimpleModel;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Entity
@Scope("prototype")
@Component
@Table(name = "UserGroup",
uniqueConstraints = {
    @UniqueConstraint(columnNames = {"userGroupName"})})
@XmlRootElement
@XmlType(name = "UserGroup")
@Database
public class UserGroup extends SimpleModel {
    @Column(length=40)
    @ModelAttr("用户组名称")
    protected String userGroupName;
    @ModelAttr("备注")
    protected String des;
    
    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinTable(name = "userGroup_role", joinColumns = {
    @JoinColumn(name = "userGroupID")}, inverseJoinColumns = {
    @JoinColumn(name = "roleID")})
    @OrderBy("id")
    @ModelAttr("用户组拥有的角色列表")
    @ModelCollRef("roleName")
    protected List<Role> roles = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.REFRESH, mappedBy = "userGroups", fetch = FetchType.LAZY)
    protected List<User> users=new ArrayList<>();

    public String getModuleCommandStr(){
        StringBuilder ids=new StringBuilder();
        roles.forEach(role -> {
            ids.append(role.getModuleCommandStr());
        });
        return ids.toString();
    }
    public String getRoleStrs(){
        if(this.roles==null || this.roles.isEmpty()) {
            return "";
        }
        StringBuilder result=new StringBuilder();
        this.roles.forEach(role -> {
            result.append("role-").append(role.getId()).append(",");
        });
        result.setLength(result.length() - 1);
        return result.toString();
    }
    /**
     * 获取授予用户组的权利
     * @return
     */
    public List<String> getAuthorities() {
        List<String> result = new ArrayList<>();
        roles.forEach(role -> {
            result.addAll(role.getAuthorities());
        });
        return result;
    }

    @XmlAttribute
    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    @XmlAttribute
    public String getUserGroupName() {
        return userGroupName;
    }

    public void setUserGroupName(String userGroupName) {
        this.userGroupName = userGroupName;
    }

    public List<Role> getRoles() {
        return Collections.unmodifiableList(this.roles);
    }
    
    public void addRole(Role role){
        this.roles.add(role);
    }
    
    public void removeRole(Role role){
        this.roles.remove(role);
    }
    
    public void clearRoles(){
        this.roles.clear();
    }
    
    @XmlTransient
    public List<User> getUsers() {
        return Collections.unmodifiableList(users);
    }

    @Override
    public String getMetaData() {
        return "用户组信息";
    }

    public static void main(String[] args){
        UserGroup obj=new UserGroup();
        //生成Action
        ActionGenerator.generate(obj.getClass());
    }
}