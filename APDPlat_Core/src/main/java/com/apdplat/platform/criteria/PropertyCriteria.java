/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.apdplat.platform.criteria;

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