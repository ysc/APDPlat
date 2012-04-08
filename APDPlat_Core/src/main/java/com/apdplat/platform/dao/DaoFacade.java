package com.apdplat.platform.dao;

import com.apdplat.platform.criteria.OrderCriteria;
import com.apdplat.platform.criteria.PageCriteria;
import com.apdplat.platform.criteria.Property;
import com.apdplat.platform.criteria.PropertyCriteria;
import com.apdplat.platform.model.Model;
import com.apdplat.platform.result.Page;
import com.apdplat.platform.util.ReflectionUtils;
import java.util.List;
import org.springframework.stereotype.Repository;
/**
 * 对任何继承自Model的类进行数据存储操作
 * @author 杨尚川
 *
 */
@Repository
public  class DaoFacade extends DaoSupport{
        public void clear(){
            em.clear();
        }

	public <T extends Model> void create(T model) {
		em.persist(model);
	}
	public <T extends Model>  T retrieve(Class<T> modelClass,Integer modelId) {
                T model=em.find(modelClass, modelId);

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
		for(Property property : properties){
			ReflectionUtils.setFieldValue(model, property.getName(), property.getValue());
		}
		update(model);
	}
	public <T extends Model>  void update(T model) {
                em.merge(model);
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
                    em.remove(model);
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
