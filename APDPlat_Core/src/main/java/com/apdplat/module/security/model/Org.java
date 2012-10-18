package com.apdplat.module.security.model;

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
import javax.persistence.Lob;
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
@XmlType(name = "Org")
@Searchable
public class Org extends Model{
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
    @Lob
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
