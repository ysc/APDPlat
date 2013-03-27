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

package com.apdplat.platform.dao;

import com.apdplat.platform.criteria.OrderCriteria;
import com.apdplat.platform.criteria.PageCriteria;
import com.apdplat.platform.criteria.Property;
import com.apdplat.platform.criteria.PropertyCriteria;
import com.apdplat.platform.model.Model;
import com.apdplat.platform.result.Page;
import com.apdplat.platform.util.ReflectionUtils;
import java.util.List;


public abstract class AbstractDao<T extends Model> extends DaoSupport implements Dao<T> {
	
	protected Class<T> modelClass;
	
	public AbstractDao(){
            this.modelClass = ReflectionUtils.getSuperClassGenricType(getClass());
        }
    
	@Override
	public void create(T model) {
		em.persist(model);
	}
	@Override
	public T retrieve(Integer modelId) {
		return em.find(modelClass, modelId);
	}

	@Override
	public void update(T model) {
		em.merge(model);
	}

	@Override
	public void update(Integer modelId, List<Property> properties) {
		T model=retrieve(modelId);
		for(Property property : properties){
			ReflectionUtils.setFieldValue(model, property.getName(), property.getValue());
		}
		update(model);
	}

	@Override
	public void delete(Integer modelId) {
		em.remove(em.getReference(modelClass, modelId));
	}

	@Override
	public Page<T> query() {
		return query(null);
	}

	@Override
	public Page<T> query(PageCriteria pageCriteria) {
		return query(pageCriteria,null,defaultOrderCriteria);
	}

	@Override
	public Page<T> query(PageCriteria pageCriteria, PropertyCriteria propertyCriteria) {
		return query(pageCriteria,propertyCriteria,defaultOrderCriteria);
	}
	
	@Override
	public Page<T> query(PageCriteria pageCriteria, PropertyCriteria propertyCriteria, OrderCriteria orderCriteria) {
		return super.queryData(modelClass,pageCriteria, propertyCriteria, orderCriteria);
	}
}