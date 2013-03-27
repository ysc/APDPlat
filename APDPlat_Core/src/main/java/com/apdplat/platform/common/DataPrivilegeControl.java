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