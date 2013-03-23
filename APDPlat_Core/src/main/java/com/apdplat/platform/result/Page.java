package com.apdplat.platform.result;

import com.apdplat.platform.log.APDPlatLogger;
import com.apdplat.platform.model.Model;
import com.apdplat.platform.util.XMLFactory;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(name = "Page")
public class Page<T extends Model> implements Serializable {
    private static final long serialVersionUID = 1L;
    protected static final APDPlatLogger log = new APDPlatLogger(Page.class);

    private long totalRecords = 0;
    private List<T> models = new ArrayList<>();

    public static <T extends Model> Page<T> newInstance(Class<T> modelClass, InputStream in) {
        XMLFactory factory = new XMLFactory(Page.class, modelClass);
        try {
            return factory.unmarshal(in);
        } catch (Exception e) {
            log.error("生成对象出错",e);
        }
        return null;
    }

    public String toXml() {
        XMLFactory factory = new XMLFactory(Page.class, getModelClass());
        String xml = null;
        try {
            xml = factory.marshal(this);
        } catch (Exception e) {
            log.error("生成XML出错",e);
        }
        return xml;
    }

    private Class getModelClass() {
        if (models.size() > 0) {
            return models.get(0).getClass();
        }
        return null;
    }

    @XmlTransient
    public long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(long totalRecords) {
        this.totalRecords = totalRecords;
    }

    @XmlElementWrapper(name = "models")
    @XmlElement(name = "model")
    public List<T> getModels() {
        return models;
    }

    public void setModels(List<T> models) {
        this.models = models;
    }
}
