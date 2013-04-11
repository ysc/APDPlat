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

package org.apdplat.platform.criteria;

import java.util.ArrayList;
import java.util.List;

/**
 * 多个属性条件
 * @author 杨尚川
 *
 */
public class PropertyCriteria {
    private String collection;
    private String object;
    private Criteria criteria = Criteria.and;

    public PropertyCriteria() {
    }

    public PropertyCriteria(Criteria criteria) {
        this.criteria = criteria;
    }
    private List<PropertyEditor> propertyEditors = new ArrayList<>();

    public List<PropertyEditor> getPropertyEditors() {
        return propertyEditors;
    }

    public void addPropertyEditor(PropertyEditor propertyEditor) {
        this.propertyEditors.add(propertyEditor);
    }

    public Criteria getCriteria() {
        return criteria;
    }

    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }
}