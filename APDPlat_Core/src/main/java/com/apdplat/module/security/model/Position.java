package com.apdplat.module.security.model;

import com.apdplat.module.module.model.Command;
import com.apdplat.module.module.model.Module;
import com.apdplat.module.module.service.ModuleService;
import com.apdplat.platform.annotation.*;
import com.apdplat.platform.generator.ActionGenerator;
import com.apdplat.platform.model.Model;
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
import org.compass.annotations.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Entity
@Scope("prototype")
@Component
@Searchable
@XmlRootElement
@XmlType(name = "Position")
public class Position extends Model{

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
    protected List<Command> commands = new ArrayList<>();
    
    public String getModuleCommandStr(){
        if(this.commands==null || this.commands.isEmpty()){
            return "";
        }
        StringBuilder ids=new StringBuilder();
        
        Set<Integer> moduleIds=new HashSet<>();
        
        for(Command command : this.commands){
            ids.append("command-").append(command.getId()).append(",");
            Module module=command.getModule();
            moduleIds.add(module.getId());
            module=module.getParentModule();
            while(module!=null){
                moduleIds.add(module.getId());
                module=module.getParentModule();
            }
        }
        for(Integer moduleId : moduleIds){
            ids.append("module-").append(moduleId).append(",");
        }
        ids=ids.deleteCharAt(ids.length()-1);
        return ids.toString();
    }
    /**
     * 获取授予岗位的权利
     * @return
     */
    public List<String> getAuthorities() {
        List<String> result = new ArrayList<>();
        for (Command command : commands) {
            Map<String,String> map=ModuleService.getCommandPathToRole(command);
            for(String role : map.values()){
                StringBuilder str = new StringBuilder();
                str.append("ROLE_MANAGER").append(role);
                result.add(str.toString());
            }
        }
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
