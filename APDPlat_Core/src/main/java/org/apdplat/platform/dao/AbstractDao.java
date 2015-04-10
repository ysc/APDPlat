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

package org.apdplat.platform.dao;

import org.apdplat.platform.criteria.OrderCriteria;
import org.apdplat.platform.criteria.PageCriteria;
import org.apdplat.platform.criteria.Property;
import org.apdplat.platform.criteria.PropertyCriteria;
import org.apdplat.platform.model.Model;
import org.apdplat.platform.result.Page;
import org.apdplat.platform.util.ReflectionUtils;
import java.util.List;


public abstract class AbstractDao<T extends Model> extends DaoSupport implements Dao<T> {
	
	protected Class<T> modelClass;
	
	public AbstractDao(){
            super(MultiDatabase.APDPlat);
            this.modelClass = ReflectionUtils.getSuperClassGenricType(getClass());
        }
        public AbstractDao(MultiDatabase multiDatabase){
            super(multiDatabase);
            this.modelClass = ReflectionUtils.getSuperClassGenricType(getClass());
        }
    
	@Override
	public void create(T model) {
		getEntityManager().persist(model);
	}
	@Override
	public T retrieve(Integer modelId) {
		return getEntityManager().find(modelClass, modelId);
	}

	@Override
	public void update(T model) {
		getEntityManager().merge(model);
	}

	@Override
	public void update(Integer modelId, List<Property> properties) {
		T model=retrieve(modelId);
		properties.forEach(property -> {
			ReflectionUtils.setFieldValue(model, property.getName(), property.getValue());
		});
		update(model);
	}

	@Override
	public void delete(Integer modelId) {
		getEntityManager().remove(getEntityManager().getReference(modelClass, modelId));
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