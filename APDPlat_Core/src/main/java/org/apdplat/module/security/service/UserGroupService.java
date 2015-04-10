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

package org.apdplat.module.security.service;

import org.apdplat.module.security.model.UserGroup;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.service.ServiceFacade;
import java.util.List;
import javax.annotation.Resource;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserGroupService {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(UserGroupService.class);
    @Resource(name="serviceFacade")
    private ServiceFacade serviceFacade;

    
    public String toAllUserGroupJson(){
        List<UserGroup> userGroups=serviceFacade.query(UserGroup.class, null).getModels();
        return toJson(userGroups);
    }
    
    public String toJson(List<UserGroup> userGroups){        
        if(userGroups==null || userGroups.isEmpty()){
            return "";
        }
        
        StringBuilder json=new StringBuilder();
        
        json.append("[");
        userGroups.forEach(userGroup -> {
            json.append("{'text':'")
                    .append(userGroup.getUserGroupName())
                    .append("','id':'userGroup-")
                    .append(userGroup.getId())
                    .append("','iconCls':'")
                    .append("role")
                    .append("'")
                    .append(",'leaf':true")
                    .append("},");
        });
        json.setLength(json.length()-1);
        json.append("]");
            
        return json.toString();
    }
}