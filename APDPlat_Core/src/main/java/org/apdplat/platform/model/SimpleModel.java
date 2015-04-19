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

package org.apdplat.platform.model;

import org.apdplat.platform.annotation.ModelAttr;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import org.apdplat.module.security.model.User;
import org.apdplat.platform.annotation.ModelAttrRef;
import org.apdplat.platform.search.annotations.SearchableComponent;

/**
 *
 * 继承这个类的模型必须和User模型存放在同一个数据库中
 * 
 * @author 杨尚川
 */
@MappedSuperclass
public abstract class SimpleModel extends Model{
    @ManyToOne
    @SearchableComponent(prefix="ownerUser_")
    @ModelAttr("数据所有者名称")
    @ModelAttrRef("username")
    protected User ownerUser;
    
    public User getOwnerUser() {
        return ownerUser;
    }

    public void setOwnerUser(User ownerUser) {
        if(this.ownerUser==null){
            this.ownerUser = ownerUser;
        }else{
            LOG.info("忽略设置OwnerUser");
        }
    }
}