package com.apdplat.platform.model;

import com.apdplat.module.security.model.User;
import com.apdplat.platform.annotation.ModelAttr;
import com.apdplat.platform.annotation.ModelAttrRef;
import com.apdplat.platform.annotation.RenderIgnore;
import com.apdplat.platform.annotation.SimpleDic;
import com.apdplat.platform.annotation.TreeDic;
import com.apdplat.platform.log.APDPlatLogger;
import com.apdplat.platform.util.ReflectionUtils;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlTransient;
import org.compass.annotations.SearchableComponent;
import org.compass.annotations.SearchableId;
import org.compass.annotations.SearchableProperty;

/**
 *
 * 所有领域对象的基类
 * 在映射的时候，如果类名或字段名与数据库的关键词冲突，则在类名后面加Table,在字段名后面加Field
 *
 * @author 杨尚川
 */
@MappedSuperclass
@EntityListeners(value = ModelListener.class)
public abstract class Model implements Serializable{
    @Transient
    @RenderIgnore
    private static final long serialVersionUID = 1L;

    @Transient
    @RenderIgnore
    protected final APDPlatLogger log = new APDPlatLogger(getClass());
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @SearchableId
    @ModelAttr("编号")
    protected Integer id;
    @SearchableProperty(format = "yyyy-MM-dd")
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @ModelAttr("创建时间")
    protected Date createTime;
    @SearchableProperty(format = "yyyy-MM-dd")
    @Column(insertable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @ModelAttr("上一次更新时间")
    protected Date updateTime;
    @Version
    @ModelAttr("更新次数")
    protected Integer version;
    
    public Model(){
        ModelMetaData.addMetaData(this);
    }

    @ManyToOne
    @SearchableComponent(prefix="ownerUser_")
    @ModelAttr("数据所有者名称")
    @ModelAttrRef("username")
    protected User ownerUser;

    public List<String> getSearchProperties() {
        List<String> list=new ArrayList<>();
        //获取所有字段，包括继承的
        List<Field> fields = ReflectionUtils.getDeclaredFields(this);
        for (Field field : fields) {
            if(field.isAnnotationPresent(SearchableProperty.class)){
                String fieldName = field.getName();
                list.add(fieldName);
            }
        }
        return list;
    }
        
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getOwnerUser() {
        return ownerUser;
    }

    public void setOwnerUser(User ownerUser) {
        if(this.ownerUser==null){
            this.ownerUser = ownerUser;
        }else{
            log.info("忽略设置OwnerUser");
        }
    }


    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @XmlTransient
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Model)) {
            return false;
        }
        Model model = (Model) obj;
        return model.getId() == this.getId();
    }

    @Override
    public int hashCode() {
        if (id == null) {
            id = -1;
        }
        return new Integer(id + 1000).hashCode();
    }

    @Override
    public String toString() {
        return this.getMetaData() + this.getId();
    }
    public List<ModelFieldData> getAllModelAttr(){
        List<ModelFieldData> list=new ArrayList<>();
        //获取所有字段，包括继承的
        List<Field> fields = ReflectionUtils.getDeclaredFields(this);
        for (Field field : fields) {
            ModelFieldData data=getFieldData(field);
            if(data!=null){
                list.add(data);
            }
        }
        return list;
    }
    public List<ModelFieldData> getModelAttr(){
        List<ModelFieldData> list=new ArrayList<>();
        //获取所有字段，不包括继承的
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            ModelFieldData data=getFieldData(field);
            if(data!=null){
                list.add(data);
            }
        }
        return list;
    }
    public List<ModelFieldData> getAllModelSearchableAttr(){
        List<ModelFieldData> list=new ArrayList<>();
        //获取所有字段，包括继承的
        List<Field> fields = ReflectionUtils.getDeclaredFields(this);
        for (Field field : fields) {
            ModelFieldData data=getSearchableFieldData(field);
            if(data!=null){
                list.add(data);
            }
        }
        return list;
    }
    public List<ModelFieldData> getModelSearchableAttr(){
        List<ModelFieldData> list=new ArrayList<>();
        //获取所有字段，不包括继承的
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            ModelFieldData data=getSearchableFieldData(field);
            if(data!=null){
                list.add(data);
            }
        }
        return list;
    }
    private ModelFieldData getSearchableFieldData(Field field){
        if(field.isAnnotationPresent(SearchableProperty.class) || field.isAnnotationPresent(SearchableComponent.class)){
            String prefix="";
            if(field.isAnnotationPresent(SearchableComponent.class)){
                prefix=field.getAnnotation(SearchableComponent.class).prefix();
            }
            String english=field.getName();
            //处理下拉项
            if(field.getType().getSimpleName().equals("DicItem")){
                english="name";
            }
            english=prefix+english;
            
            return getModelFieldData(english,field);
        }
        return null;
    }
    private ModelFieldData getFieldData(Field field){
        if(field.isAnnotationPresent(ModelAttr.class)){
            String english=field.getName();
            return getModelFieldData(english,field);
        }
        return null;
    }
    private ModelFieldData getModelFieldData(String english, Field field){
        String chinese=field.getAnnotation(ModelAttr.class).value();
        ModelFieldData data=new ModelFieldData();
        data.setChinese(chinese);
        data.setEnglish(english);
        data.setSimpleDic("");
        data.setTreeDic("");
        if(field.isAnnotationPresent(SimpleDic.class)){
            String dic=field.getAnnotation(SimpleDic.class).value();
            data.setSimpleDic(dic);
        }
        if(field.isAnnotationPresent(TreeDic.class)){
            String dic=field.getAnnotation(TreeDic.class).value();
            data.setTreeDic(dic);
        }
        String valueClass=field.getType().getSimpleName();
        if("Timestamp".equals(valueClass) || "Date".equals(valueClass)){
            data.setType("Date");
        }else{
            data.setType(valueClass);
        }
        return data;
    }
    public abstract String getMetaData();
}
