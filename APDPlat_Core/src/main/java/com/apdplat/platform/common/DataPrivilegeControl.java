package com.apdplat.platform.common;

import com.apdplat.module.system.service.PropertyHolder;
import com.apdplat.platform.model.Model;
import javax.persistence.Entity;

/**
 *
 * @author ysc
 */
public class DataPrivilegeControl {
    private static String[] excludes=null;
    static{
        excludes=PropertyHolder.getProperty("data.privilege.control.exclude").split(",");
    }

    protected boolean needPrivilege(String modelClass){
        for(String exclude : excludes){
            if(exclude.equals(modelClass)){
                return false;
            }
        }
        return true;
    }
    protected <T extends Model> boolean needPrivilege(Class<T> modelClass){
        String entity=getEntityName(modelClass);
        return needPrivilege(entity);
    }

    /**
     * 获取实体的名称
     * @param clazz
     * @return
     */
    protected String getEntityName(Class<? extends Model> clazz) {
        String entityname = clazz.getSimpleName();

        Entity entity = clazz.getAnnotation(Entity.class);
        if (entity != null && entity.name() != null && !"".equals(entity.name())) {
            entityname = entity.name();
        }
        return entityname;
    }
}
