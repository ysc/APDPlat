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
import org.apdplat.platform.dao.DaoFacade;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.model.Model;
import org.apdplat.platform.result.Page;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * 对任何继承自Model的类进行数据存储操作
 * @author 杨尚川
 *
 */
@Service
public  class ServiceFacade{
        protected final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(getClass());   
    
	@Resource(name="daoFacade")
	private DaoFacade dao = null;     

        public void setDao(DaoFacade dao) {
            this.dao = dao;
        }

        public void clear(){
            dao.clear();
        }
        /**
         * 批量保存，批量提交，显著提升性能
         * @param <T>
         * @param models 
         */
	@Transactional
	public <T extends Model> void create(List<T> models) {
            for(T model : models){
		dao.create(model);
            }
	}

	@Transactional
	public <T extends Model> void create(T model) {
		dao.create(model);
	}

	public <T extends Model> T retrieve(Class<T> modelClass,Integer modelId) {
		T model = dao.retrieve(modelClass,modelId);

                if(model==null){
                    return null;
                }
                return model;
	}

	@Transactional
	public <T extends Model> void update(T model) {
		dao.update(model);
	}

	@Transactional
	public <T extends Model> void update(Class<T> modelClass,Integer modelId, List<Property> properties) {
		dao.update(modelClass,modelId, properties);
	}

	@Transactional
	public <T extends Model> void delete(Class<T> modelClass,Integer modelId) {
		dao.delete(modelClass,modelId);
	}
	@Transactional
	public <T extends Model> List<Integer> delete(Class<T> modelClass,Integer[] modelIds) {
                List<Integer> ids=new ArrayList<>();
		for(Integer modelId : modelIds){
			try{
				this.delete(modelClass,modelId);
                                ids.add(modelId);
			}catch(Exception e){
				LOG.error("删除模型出错",e);
			}
		}
                return ids;
	}

	public <T extends Model> Page<T> query(Class<T> modelClass) {
		Page<T> page = dao.query(modelClass,null);
                return page;
	}

	public <T extends Model> Page<T> query(Class<T> modelClass,PageCriteria pageCriteria) {
		Page<T> page = dao.query(modelClass,pageCriteria,null);
                return page;
	}

	public <T extends Model> Page<T> query(Class<T> modelClass,PageCriteria pageCriteria, PropertyCriteria propertyCriteria) {
		Page<T> page = dao.query(modelClass,pageCriteria, propertyCriteria);
                return page;
	}

	public <T extends Model> Page<T> query(Class<T> modelClass,PageCriteria pageCriteria, PropertyCriteria propertyCriteria, OrderCriteria orderCriteria) {
                Page<T> page = dao.query(modelClass,pageCriteria, propertyCriteria,orderCriteria);
                return page;
	}

	@Transactional
        public <T extends Model> Page<T> search(String queryString,PageCriteria pageCriteria,Class<T> modelClass) {
            Page<T> page = dao.search(queryString, pageCriteria, modelClass);
            return page;
        }
}