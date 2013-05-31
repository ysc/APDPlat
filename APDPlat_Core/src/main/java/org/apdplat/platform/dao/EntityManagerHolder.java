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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author 杨尚川
 */
public class EntityManagerHolder {
    private MultiDatabase multiDatabase;
    public EntityManagerHolder(MultiDatabase multiDatabase){
        this.multiDatabase=multiDatabase;
    }
    
    //遗憾的是：这里的unitName用不了配置文件中的变量了
    @PersistenceContext(unitName = "apdplat")
    private EntityManager em;
    @PersistenceContext(unitName = "apdplatForLog")
    private EntityManager emForLog;
    
    public EntityManager getEntityManager(){
        if(multiDatabase == MultiDatabase.APDPlat){
            return em;
        }
        if(multiDatabase == MultiDatabase.APDPlatForLog){
            return emForLog;
        }
        return em;
    }
}
