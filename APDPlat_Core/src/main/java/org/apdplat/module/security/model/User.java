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

import org.apdplat.module.module.model.Command;
import org.apdplat.module.module.model.Module;
import org.apdplat.platform.annotation.ModelAttr;
import org.apdplat.platform.annotation.ModelAttrRef;
import org.apdplat.platform.annotation.ModelCollRef;
import org.apdplat.platform.generator.ActionGenerator;
import org.apdplat.platform.service.ServiceFacade;
import org.apdplat.platform.util.SpringContextUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.apdplat.platform.annotation.Database;
import org.apdplat.platform.model.SimpleModel;
import org.compass.annotations.*;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Entity
@Scope("prototype")
@Component
@Searchable
@Table(name = "UserTable",
uniqueConstraints = {
    @UniqueConstraint(columnNames = {"username"})})
@XmlRootElement
@XmlType(name = "User")
@Database
public class User extends SimpleModel  implements UserDetails{
    @ManyToOne
    @SearchableComponent(prefix="org_")
    @ModelAttr("组织架构")
    @ModelAttrRef("orgName")
    protected Org org;

    //用户名不分词
    @SearchableProperty(index=Index.NOT_ANALYZED)
    @ModelAttr("用户名")
    protected String username;

    @SearchableProperty
    @ModelAttr("姓名")
    protected String realName;

    @ModelAttr("密码")
    protected String password;

    @SearchableProperty
    @ModelAttr("备注")
    protected String des;
    
    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinTable(name = "user_role", joinColumns = {
    @JoinColumn(name = "userID")}, inverseJoinColumns = {
    @JoinColumn(name = "roleID")})
    @OrderBy("id")
    @ModelAttr("用户拥有的角色列表")
    @ModelCollRef("roleName")
    protected List<Role> roles = new ArrayList<>();
    
    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinTable(name = "user_userGroup", joinColumns = {
    @JoinColumn(name = "userID")}, inverseJoinColumns = {
    @JoinColumn(name = "userGroupID")})
    @OrderBy("id")
    @ModelAttr("用户拥有的用户组列表")
    @ModelCollRef("userGroupName")
    protected List<UserGroup> userGroups = new ArrayList<>();
    
    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinTable(name = "user_position", joinColumns = {
    @JoinColumn(name = "userID")}, inverseJoinColumns = {
    @JoinColumn(name = "positionID")})
    @OrderBy("id")
    @ModelAttr("用户拥有的岗位列表")
    @ModelCollRef("positionName")
    protected List<Position> positions = new ArrayList<>();

    @ModelAttr("账号过期")
    protected boolean accountexpired = false;
    @ModelAttr("账户锁定")
    protected boolean accountlocked = false;
    @ModelAttr("信用过期")
    protected boolean credentialsexpired = false;
    @ModelAttr("账户可用")
    protected boolean enabled = true;
    
    /**
     * 用户登录验证
     * 具体的验证规则就写在这里
     * 
     * @return 验证结果，null为验证通过，非null则为验证未通过的原因
     */
    public String loginValidate(){
        String message = null;
        if(!isEnabled()){
            message = "用户账号被禁用";
        }
        if(!isAccountNonExpired()){
            message = "用户帐号已过期";
        }
        if(!isAccountNonLocked()){
            message = "用户帐号已被锁定";
        }
        if(!isCredentialsNonExpired()){
            message = "用户凭证已过期";
        }
        if(getAuthorities() == null){
            message = "用户帐号未被授予任何权限";
        }
        return message;
    }
    
    /**
     * 用户是否为超级管理员
     * @return
     */
    public boolean isSuperManager(){
        if(this.roles != null && !this.roles.isEmpty()) {
            for(Role role : this.roles){
                if(role.isSuperManager()) {
                    return true;
                }
            }
        }
        if(this.userGroups != null && !this.userGroups.isEmpty()){
            for(UserGroup userGroup : this.userGroups){
                for(Role role : userGroup.getRoles()){
                    if(role.isSuperManager()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public String getRoleStrs(){
        if(this.roles==null || this.roles.isEmpty()) {
            return "";
        }
        StringBuilder result=new StringBuilder();
        for(Role role : this.roles){
            result.append("role-").append(role.getId()).append(",");
        }
        result=result.deleteCharAt(result.length()-1);
        return result.toString();
    }
    
    public String getPositionStrs(){
        if(this.positions==null || this.positions.isEmpty()) {
            return "";
        }
        StringBuilder result=new StringBuilder();
        for(Position position : this.positions){
            result.append("position-").append(position.getId()).append(",");
        }
        result=result.deleteCharAt(result.length()-1);
        return result.toString();
    }
    
    public String getUserGroupStrs(){
        if(this.userGroups==null || this.userGroups.isEmpty()) {
            return "";
        }
        StringBuilder result=new StringBuilder();
        for(UserGroup userGroup : this.userGroups){
            result.append("userGroup-").append(userGroup.getId()).append(",");
        }
        result=result.deleteCharAt(result.length()-1);
        return result.toString();
    }

    public List<Command> getCommand() {
        List<Command> result = new ArrayList<>();

        if(this.roles != null && !this.roles.isEmpty()) {
            //如果用户为超级管理员
            for (Role role : this.roles) {
                if (role.isSuperManager()) {
                    return getAllCommand();
                }
            }
            //如果用户不是超级管理员则进行一下处理
            for (Role role : this.roles) {
                result.addAll(role.getCommands());
            }
        }
        if(this.userGroups != null && !this.userGroups.isEmpty()){
            for(UserGroup userGroup : this.userGroups){
                //如果用户为超级管理员
                for(Role role : userGroup.getRoles()){
                    if(role.isSuperManager()) {
                        return getAllCommand();
                    }
                }
                //如果用户不是超级管理员则进行一下处理
                for(Role role : userGroup.getRoles()){
                    result.addAll(role.getCommands());
                }
            }
        }
        if(this.positions != null && !this.positions.isEmpty()) {
            for (Position position : this.positions) {
                result.addAll(position.getCommands());
            }
        }
        return result;
    }

    private List<Command> getAllCommand(){
    	ServiceFacade serviceFacade = SpringContextUtils.getBean("serviceFacade");
        List<Command> allCommand = serviceFacade.query(Command.class).getModels();
        return allCommand;
    }

    public List<Module> getModule() {
        List<Module> result = new  ArrayList<>();

        if(this.roles != null && !this.roles.isEmpty()) {
            //如果用户为超级管理员
            for (Role role : this.roles) {
                if (role.isSuperManager()) {
                    return getAllModule();
                }
            }
            //如果用户不是超级管理员则进行一下处理
            for (Role role : this.roles) {
                result.addAll(assemblyModule(role.getCommands()));
            }
        }
        if(this.userGroups != null && !this.userGroups.isEmpty()){
            for(UserGroup userGroup : this.userGroups){
                //如果用户为超级管理员
                for(Role role : userGroup.getRoles()){
                    if(role.isSuperManager()) {
                        return getAllModule();
                    }
                }
                //如果用户不是超级管理员则进行一下处理
                for(Role role : userGroup.getRoles()){
                    result.addAll(assemblyModule(role.getCommands()));
                }
            }
        }
        if(this.positions != null && !this.positions.isEmpty()) {
            for (Position position : this.positions) {
                result.addAll(assemblyModule(position.getCommands()));
            }
        }

        return result;
    }
    private List<Module> getAllModule(){
    	ServiceFacade serviceFacade = SpringContextUtils.getBean("serviceFacade");
        List<Module> allModule = serviceFacade.query(Module.class).getModels();
        return allModule;
    }
    private List<Module> assemblyModule(List<Command> commands){
        List<Module> modules=new ArrayList<>();
        if(commands==null) {
            return modules;
        }
        
        for(Command command : commands){
            if(command!=null){
                Module module=command.getModule();
                if(module!=null){
                    modules.add(module);
                    assemblyModule(modules,module);
                }
            }
        }
        return modules;
    }
    private void assemblyModule(List<Module> modules,Module module){
        if(module!=null){
            Module parentModule=module.getParentModule();
            if(parentModule!=null){
                modules.add(parentModule);
                assemblyModule(modules,parentModule);
            }
        }
    }
    public String getAuthoritiesStr(){
        StringBuilder result=new StringBuilder();
        for(GrantedAuthority auth : getAuthorities()){
            result.append(auth.getAuthority()).append(",");
        }
        return result.toString();
    }
    /**
     * 获取授予用户的权利
     * @return
     */
    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> grantedAuthArray=new HashSet<>();

        LOG.debug("user privilege:");
        //如果用户是超级管理员，则只需加入ROLE_SUPERMANAGER标识
        //就不用对其他的权限对象进行检查
        if(isSuperManager()){
            grantedAuthArray.add(new SimpleGrantedAuthority("ROLE_SUPERMANAGER"));
            LOG.debug("ROLE_SUPERMANAGER");
        }else{
            if(this.roles != null && !this.roles.isEmpty()) {
                LOG.debug("     roles:");
                for (Role role : this.roles) {
                    for (String priv : role.getAuthorities()) {
                        LOG.debug(priv);
                        grantedAuthArray.add(new SimpleGrantedAuthority(priv.toUpperCase()));
                    }
                }
            }
            if(this.userGroups != null && !this.userGroups.isEmpty()){
                LOG.debug("     userGroups:");
                for(UserGroup userGroup : this.userGroups){
                    for(Role role : userGroup.getRoles()){
                        for (String priv : role.getAuthorities()) {
                            LOG.debug(priv);
                            grantedAuthArray.add(new SimpleGrantedAuthority(priv.toUpperCase()));
                        }
                    }
                }
            }        
            if(this.positions != null && !this.positions.isEmpty()) {
                LOG.debug("     positions:");
                for (Position position : this.positions) {
                    for (String priv : position.getAuthorities()) {
                        LOG.debug(priv);
                        grantedAuthArray.add(new SimpleGrantedAuthority(priv.toUpperCase()));
                    }
                }
            }
        }
        if(grantedAuthArray.isEmpty()){
            return null;
        }
        grantedAuthArray.add(new SimpleGrantedAuthority("ROLE_MANAGER"));
        LOG.debug("ROLE_MANAGER");
        return grantedAuthArray;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !accountexpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountlocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !credentialsexpired;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    @Override
    @XmlAttribute
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setAccountexpired(boolean accountexpired) {
        this.accountexpired = accountexpired;
    }

    public void setAccountlocked(boolean accountlocked) {
        this.accountlocked = accountlocked;
    }

    public void setCredentialsexpired(boolean credentialsexpired) {
        this.credentialsexpired = credentialsexpired;
    }

    @XmlTransient
    public List<UserGroup> getUserGroups() {
        return Collections.unmodifiableList(this.userGroups);
    }

    public void setUserGroups(List<UserGroup> userGroups) {
        this.userGroups = userGroups;
    }

    public void addUserGroup(UserGroup userGroup) {
        this.userGroups.add(userGroup);
    }

    public void removeUserGroup(UserGroup userGroup) {
        this.userGroups.remove(userGroup);
    }

    public void clearUserGroup() {
        this.userGroups.clear();
    }

    @XmlTransient
    public List<Role> getRoles() {
        return Collections.unmodifiableList(this.roles);
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    public void clearRole() {
        this.roles.clear();
    }
    

    @XmlTransient
    public List<Position> getPositions() {
        return Collections.unmodifiableList(this.positions);
    }

    public void addPosition(Position position) {
        this.positions.add(position);
    }

    public void removePosition(Position position) {
        this.positions.remove(position);
    }

    public void clearPosition() {
        this.positions.clear();
    }
            
    @Override
    @XmlAttribute
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @XmlAttribute
    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @XmlTransient
    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    @Override
    @XmlAttribute
    public String getUsername() {
        return username;
    }
    @Override
    public String getMetaData() {
        return "用户信息";
    }

    public static void main(String[] args){
        User obj=new User();
        //生成Action
        ActionGenerator.generate(obj.getClass());
    }
}