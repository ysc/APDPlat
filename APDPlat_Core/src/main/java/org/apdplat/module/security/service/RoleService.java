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

import org.apdplat.module.security.model.Role;
import org.apdplat.platform.criteria.Criteria;
import org.apdplat.platform.criteria.Operator;
import org.apdplat.platform.criteria.PropertyCriteria;
import org.apdplat.platform.criteria.PropertyEditor;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.result.Page;
import org.apdplat.platform.service.ServiceFacade;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(RoleService.class);
    @Resource(name="serviceFacade")
    private ServiceFacade serviceFacade;

    public static List<String> getChildNames(Role role){
        List<String> names=new ArrayList<>();
        List<Role> child=role.getChild();
        child.forEach(item -> {
            names.add(item.getRoleName());
            names.addAll(getChildNames(item));
        });
        return names;
    }
    public static List<Integer> getChildIds(Role role){
        List<Integer> ids=new ArrayList<>();
        List<Role> child=role.getChild();
        child.forEach(item -> {
            ids.add(item.getId());
            ids.addAll(getChildIds(item));
        });
        return ids;
    }
    public static boolean isParentOf(Role parent,Role child){
        Role role=child.getParent();
        while(role!=null){
            if(role.getId()==parent.getId()){
                return true;
            }
            role=role.getParent();
        }
        return false;
    }
    
    public String toRootJson(boolean recursion){
        Role rootRole=getRootRole();
        if(rootRole==null){
            LOG.error("获取根角色失败！");
            return "";
        }
        StringBuilder json=new StringBuilder();
        json.append("[");

        json.append("{'text':'")
            .append(rootRole.getRoleName())
            .append("','id':'role-")
            .append(rootRole.getId());
            if(rootRole.getChild().isEmpty()){
                json.append("','leaf':true,'cls':'file'");
            }else{
                json.append("','leaf':false,'cls':'folder'");
                
                if (recursion) {
                    rootRole.getChild().forEach(item -> {
                        json.append(",children:").append(toJson(item.getId(), recursion));
                    });
                }
            }
        json.append("}");
        json.append("]");
        
        return json.toString();
    }
    public String toJson(int roleId, boolean recursion){
        Role role=serviceFacade.retrieve(Role.class, roleId);
        if(role==null){
            LOG.error("获取ID为 "+roleId+" 的角色失败！");
            return "";
        }
        List<Role> child=role.getChild();
        if(child.isEmpty()){
            return "";
        }
        StringBuilder json=new StringBuilder();
        json.append("[");

        child.forEach(item -> {
            json.append("{'text':'")
                .append(item.getRoleName())
                .append("','id':'role-")
                .append(item.getId());
                if(item.getChild().isEmpty()){
                    json.append("','leaf':true,'cls':'file'");
                }else{
                    json.append("','leaf':false,'cls':'folder'");
                    if (recursion) {
                        json.append(",children:").append(toJson(item.getId(), recursion));
                    }
                }
           json .append("},");
        });
        //删除最后一个,号，添加一个]号
        json.setLength(json.length()-1);
        json.append("]");

        return json.toString();
    }
    public Role getRootRole(){
        PropertyCriteria propertyCriteria = new PropertyCriteria(Criteria.or);
        propertyCriteria.addPropertyEditor(new PropertyEditor("roleName", Operator.eq, "String","角色"));
        Page<Role> page = serviceFacade.query(Role.class, null, propertyCriteria);
        if (page.getTotalRecords() == 1) {
            return page.getModels().get(0);
        }
        return null;
    }
}