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
