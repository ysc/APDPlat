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

package org.apdplat.platform.result;

import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.model.Model;
import org.apdplat.platform.util.XMLFactory;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.apdplat.platform.log.APDPlatLoggerFactory;

@XmlRootElement
@XmlType(name = "Page")
public class Page<T extends Model> implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(Page.class);

    private long totalRecords = 0;
    private List<T> models = new ArrayList<>();

    public static <T extends Model> Page<T> newInstance(Class<T> modelClass, InputStream in) {
        XMLFactory factory = new XMLFactory(Page.class, modelClass);
        try {
            return factory.unmarshal(in);
        } catch (Exception e) {
            LOG.error("生成对象出错",e);
        }
        return null;
    }

    public String toXml() {
        XMLFactory factory = new XMLFactory(Page.class, getModelClass());
        String xml = null;
        try {
            xml = factory.marshal(this);
        } catch (Exception e) {
            LOG.error("生成XML出错",e);
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