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

package org.apdplat.platform.common;

import org.apdplat.module.system.service.PropertyHolder;
import org.apdplat.platform.model.Model;
import javax.persistence.Entity;
import org.apdplat.platform.dao.EntityManagerHolder;
import org.apdplat.platform.dao.MultiDatabase;

/**
 *
 * @author 杨尚川
 */
public abstract class DataPrivilegeControl extends EntityManagerHolder{
    private static String[] excludes=null;
    static{
        excludes=PropertyHolder.getProperty("data.privilege.control.exclude").split(",");
    }
    
    public DataPrivilegeControl(){
        super(MultiDatabase.APDPlat);
    }
    public DataPrivilegeControl(MultiDatabase multiDatabase){
        super(multiDatabase);
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