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

package org.apdplat.platform.service;

import org.apdplat.platform.criteria.OrderCriteria;
import org.apdplat.platform.criteria.PageCriteria;
import org.apdplat.platform.criteria.Property;
import org.apdplat.platform.criteria.PropertyCriteria;
import org.apdplat.platform.dao.Dao;
import org.apdplat.platform.model.Model;
import org.apdplat.platform.result.Page;
import org.apdplat.platform.util.ReflectionUtils;
import org.apdplat.platform.util.SpringContextUtils;
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