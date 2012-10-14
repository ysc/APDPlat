package com.apdplat.module.security.model;

import com.apdplat.platform.annotation.*;
import com.apdplat.platform.generator.ActionGenerator;
import com.apdplat.platform.model.Model;
import java.util.ArrayList;
import java.util.List;
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
    protected List<Position> child = new ArrayList<Position>();

    

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