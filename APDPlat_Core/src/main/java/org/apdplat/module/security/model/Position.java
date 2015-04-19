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
import org.apdplat.module.module.service.ModuleService;
import org.apdplat.platform.annotation.*;
import org.apdplat.platform.generator.ActionGenerator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.apdplat.platform.model.SimpleModel;
import org.apdplat.platform.search.annotations.Searchable;
import org.apdplat.platform.search.annotations.SearchableComponent;
import org.apdplat.platform.search.annotations.SearchableProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Entity
@Scope("prototype")
@Component
@Searchable
@XmlRootElement
@XmlType(name = "Position")
@Database
public class Position extends SimpleModel{

    @SearchableProperty
    @ModelAttr("岗位名称")
    protected String positionName;

    @ManyToOne
    @SearchableComponent
    @ModelAttr("上级岗位")
    @ModelAttrRef("positionName")
    protected Position parent;

    @RenderIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "parent")
    @OrderBy("id DESC")
    @ModelAttr("下级岗位")
    @ModelCollRef("positionName")
    protected List<Position> child = new ArrayList<>();

    /**
     * 职位拥有的命令
     */
    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinTable(name = "position_command", joinColumns = {
    @JoinColumn(name = "positionID")}, inverseJoinColumns = {
    @JoinColumn(name = "commandID")})
    @OrderBy("id")
    @ModelAttr("岗位拥有的命令列表")
    @ModelCollRef("chinese")
    protected List<Command> commands = new ArrayList<>();
    
    @ManyToMany(cascade = CascadeType.REFRESH, mappedBy = "positions", fetch = FetchType.LAZY)
    protected List<User> users=new ArrayList<>();
    
    public String getModuleCommandStr(){
        if(this.commands==null || this.commands.isEmpty()){
            return "";
        }
        StringBuilder ids=new StringBuilder();
        
        Set<Integer> moduleIds=new HashSet<>();

        this.commands.forEach(command -> {
            ids.append("command-").append(command.getId()).append(",");
            Module module=command.getModule();
            moduleIds.add(module.getId());
            module=module.getParentModule();
            while(module!=null){
                moduleIds.add(module.getId());
                module=module.getParentModule();
            }
        });
        moduleIds.forEach(moduleId -> {
            ids.append("module-").append(moduleId).append(",");
        });
        ids.setLength(ids.length()-1);
        return ids.toString();
    }
    /**
     * 获取授予岗位的权利
     * @return
     */
    public List<String> getAuthorities() {
        List<String> result = new ArrayList<>();
        commands.forEach(command -> {
            Map<String,String> map=ModuleService.getCommandPathToRole(command);
            map.values().forEach(role -> {
                StringBuilder str = new StringBuilder();
                str.append("ROLE_MANAGER").append(role);
                result.add(str.toString());
            });
        });
        return result;
    }

    @XmlAttribute
    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    @XmlTransient
    public Position getParent() {
        return parent;
    }

    public void setParent(Position parent) {
        this.parent = parent;
    }

    @XmlElementWrapper(name = "subPositions")
    @XmlElement(name = "position")
    public List<Position> getChild() {
        return this.child;
    }

    public void addChild(Position child) {
        this.child.add(child);
    }

    public void removeChild(Position child) {
        this.child.remove(child);
    }

    public void clearChild() {
        this.child.clear();
    }

    public void setCommands(List<Command> commands) {
        this.commands = commands;
    }

    @XmlTransient
    public List<Command> getCommands() {
        return Collections.unmodifiableList(commands);
    }
  
    public void addCommands(Command command) {
        this.commands.add(command);
    }
  
    public void removeCommand(Command command) {
        this.commands.remove(command);
    }
    public void clearCommand() {
        commands.clear();
    }
    @XmlTransient
    public List<User> getUsers() {
        return Collections.unmodifiableList(users);
    }
    @Override
    public String getMetaData() {
        return "岗位";
    }
    public static void main(String[] args){
        Position obj=new Position();
        //生成Action
        ActionGenerator.generate(obj.getClass());
    }
}