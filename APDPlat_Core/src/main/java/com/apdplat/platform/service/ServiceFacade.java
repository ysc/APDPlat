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

package com.apdplat.platform.service;

import com.apdplat.platform.criteria.OrderCriteria;
import com.apdplat.platform.criteria.PageCriteria;
import com.apdplat.platform.criteria.Property;
import com.apdplat.platform.criteria.PropertyCriteria;
import com.apdplat.platform.dao.DaoFacade;
import com.apdplat.platform.log.APDPlatLogger;
import com.apdplat.platform.model.Model;
import com.apdplat.platform.result.Page;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * 对任何继承自Model的类进行数据存储操作
 * @author 杨尚川
 *
 */
@Service
public  class ServiceFacade{
        protected final APDPlatLogger log = new APDPlatLogger(getClass());
    
	@Resource(name="daoFacade")
	private DaoFacade dao = null;

        public void clear(){
            dao.clear();
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
				log.error("删除模型出错",e);
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