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
import org.springframework.stereotype.Repository;
/**
 * 对任何继承自Model的类进行数据存储操作
 * @author 杨尚川
 *
 */
@Repository
public  class DaoFacade extends DaoSupport{
        /**
         * 使用默认数据库
         */
        public DaoFacade(){
            super(MultiDatabase.APDPlat);
        }
        /**
         * 使用默认日志数据库
         * @param multiDatabase 
         */
        public DaoFacade(MultiDatabase multiDatabase){
            super(multiDatabase);
        }
        
        public void clear(){
            getEntityManager().clear();
        }

	public <T extends Model> void create(T model) {
		getEntityManager().persist(model);
	}
	public <T extends Model>  T retrieve(Class<T> modelClass,Integer modelId) {
                T model=getEntityManager().find(modelClass, modelId);

                return model;
            /*
                //权限控制
                User user=UserHolder.getCurrentLoginUser();
                if(user!=null && !user.isSuperManager() && needPrivilege(modelClass) && model.getOwnerUser()!=null){
                    if(user.getId().intValue()==model.getOwnerUser().getId().intValue()){
                        return model;
                    }
                    if(OrgService.isParentOf(user.getOrg(), model.getOwnerUser().getOrg())){
                        return model;
                    }
                    return null;
                }else{
                    return model;
                }
             * 
             */
	}


	public <T extends Model>  void update(Class<T> modelClass,Integer modelId, List<Property> properties) {
		T model=retrieve(modelClass,modelId);
        properties.forEach(property -> {
			ReflectionUtils.setFieldValue(model, property.getName(), property.getValue());
		});
		update(model);
	}
	public <T extends Model>  void update(T model) {
                getEntityManager().merge(model);
            /*
                User user=UserHolder.getCurrentLoginUser();
                if(user!=null && !user.isSuperManager() && needPrivilege(model.getClass())){
                    if(user.getId().intValue()==model.getOwnerUser().getId().intValue()){
                        em.merge(model);
                    }
                    if(OrgService.isParentOf(user.getOrg(), model.getOwnerUser().getOrg())){
                        em.merge(model);
                    }
                }else{
                    em.merge(model);
                }
             * 
             */
	}

	public <T extends Model> void delete(Class<T> modelClass,Integer modelId) {
                T model=retrieve(modelClass,modelId);
                if(model!=null){
                    getEntityManager().remove(model);
                }
                /*
                User user=UserHolder.getCurrentLoginUser();
                if(user!=null && !user.isSuperManager() && needPrivilege(modelClass)){
                    if(user.getId().intValue()==model.getOwnerUser().getId().intValue()){
                        em.remove(model);
                    }
                    if(OrgService.isParentOf(user.getOrg(), model.getOwnerUser().getOrg())){
                        em.remove(model);
                    }
                }else{
                    em.remove(model);
                }
                 * 
                 */
	}

	public <T extends Model>  Page<T> query(Class<T> modelClass) {
		return query(modelClass, null);
	}

	public <T extends Model>  Page<T> query(Class<T> modelClass,PageCriteria pageCriteria) {
		
		return query(modelClass, pageCriteria,null,defaultOrderCriteria);
	}

	public <T extends Model>  Page<T> query(Class<T> modelClass,PageCriteria pageCriteria, PropertyCriteria propertyCriteria) {
		return query(modelClass, pageCriteria,propertyCriteria,defaultOrderCriteria);
	}
	
	public <T extends Model>  Page<T> query(Class<T> modelClass,PageCriteria pageCriteria, PropertyCriteria propertyCriteria, OrderCriteria orderCriteria) {
		return super.queryData(modelClass,pageCriteria, propertyCriteria, orderCriteria);
	}
}