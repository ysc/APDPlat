package com.apdplat.platform.service;

import com.apdplat.platform.criteria.OrderCriteria;
import com.apdplat.platform.criteria.PageCriteria;
import com.apdplat.platform.criteria.Property;
import com.apdplat.platform.criteria.PropertyCriteria;
import com.apdplat.platform.dao.Dao;
import com.apdplat.platform.model.Model;
import com.apdplat.platform.result.Page;
import com.apdplat.platform.util.ReflectionUtils;
import com.apdplat.platform.util.SpringContextUtils;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractService<T extends Model, D extends Dao<T>> implements Service<T> {
	protected D dao = null;
    @Resource(name="springContextUtils")
    protected SpringContextUtils springContextUtils;

	@PostConstruct
	private void initDao() {
		if (this.dao == null) {
			String modelName = ReflectionUtils.getSuperClassGenricType(getClass()).getSimpleName();
			StringBuilder daoName = new StringBuilder();
			daoName.append(Character.toLowerCase(modelName.charAt(0))).append(modelName.substring(1)).append("Dao");
			//返回值必须强制转换为D
			this.dao = (D)springContextUtils.getBean(daoName.toString());
		}
	}

	@Override
	@Transactional
	public void create(T model) {
		dao.create(model);
	}

	@Override
	public T retrieve(Integer modelId) {
		return dao.retrieve(modelId);
	}

	@Override
	@Transactional
	public void update(T model) {
		dao.update(model);
	}

	@Override
	@Transactional
	public void update(Integer modelId, List<Property> properties) {
		dao.update(modelId, properties);
	}

	@Override
	@Transactional
	public void delete(Integer modelId) {
		dao.delete(modelId);
	}

	@Override
	@Transactional
	public List<Exception> delete(Integer[] modelIds) {
		List<Exception> errors=new ArrayList<>();
		for(Integer modelId : modelIds){
			try{
				this.delete(modelId);
			}catch(Exception e){
				errors.add(e);
			}
		}
		return errors;
	}

	@Override
	public Page<T> query() {
		return dao.query();
	}

	@Override
	public Page<T> query(PageCriteria pageCriteria) {
		return dao.query(pageCriteria);
	}

	@Override
	public Page<T> query(PageCriteria pageCriteria, PropertyCriteria propertyCriteria) {
		return dao.query(pageCriteria, propertyCriteria);
	}

	@Override
	public Page<T> query(PageCriteria pageCriteria, PropertyCriteria propertyCriteria, OrderCriteria sortCriteria) {
		return dao.query(pageCriteria, propertyCriteria,sortCriteria);
	}
}
