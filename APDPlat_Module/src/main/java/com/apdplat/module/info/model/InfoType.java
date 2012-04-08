package com.apdplat.module.info.model;

import com.apdplat.platform.generator.ActionGenerator;
import com.apdplat.platform.annotation.ModelAttr;
import com.apdplat.platform.annotation.ModelAttrRef;
import com.apdplat.platform.annotation.RenderIgnore;
import com.apdplat.platform.model.Model;
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
import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableComponent;
import org.compass.annotations.SearchableProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Entity
@Scope("prototype")
@Component
@XmlRootElement
@XmlType(name = "InfoType")
@Searchable
public class InfoType extends Model{
    @SearchableProperty
    @ModelAttr("类别名称")
    protected String infoTypeName;
    @ModelAttr("顺序号")
    protected int orderNum;
    @ManyToOne
    @SearchableComponent
    @ModelAttr("父类别")
    @ModelAttrRef("infoTypeName")
    protected InfoType parent;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "parent")
    @OrderBy("orderNum ASC")
    @RenderIgnore
    @ModelAttr("子类别")
    protected List<InfoType> child=new ArrayList<InfoType>();
    

    @XmlElementWrapper(name = "subInfoTypes")
    @XmlElement(name = "infoType")
    public List<InfoType> getChild() {
        return child;
    }

    public void setChild(List<InfoType> child) {
        this.child = child;
    }

    @XmlTransient
    public InfoType getParent() {
        return parent;
    }

    public void setParent(InfoType parent) {
        this.parent = parent;
    }

    @XmlAttribute
    public String getInfoTypeName() {
        return infoTypeName;
    }

    public void setInfoTypeName(String infoTypeName) {
        this.infoTypeName = infoTypeName;
    }


    @XmlAttribute
    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    @Override
    public String getMetaData() {
        return "新闻类别";
    }
    public static void main(String[] args){
        InfoType obj=new InfoType();
        //生成Action
        ActionGenerator.generate(obj.getClass());
    }
}
