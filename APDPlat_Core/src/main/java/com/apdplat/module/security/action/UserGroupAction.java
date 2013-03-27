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

package com.apdplat.module.security.action;

import com.apdplat.module.security.model.Role;
import com.apdplat.module.security.model.User;
import com.apdplat.module.security.model.UserGroup;
import com.apdplat.module.security.service.UserGroupService;
import com.apdplat.module.security.service.UserHolder;
import com.apdplat.platform.action.ExtJSSimpleAction;
import com.apdplat.platform.util.Struts2Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Scope("prototype")
@Controller
@Namespace("/security")
public class UserGroupAction extends ExtJSSimpleAction<UserGroup> {
    @Resource(name="userGroupService")
    private UserGroupService userGroupService;
    private List<Role> roles = null;
    
    public String store(){     
        String json = userGroupService.toAllUserGroupJson();
        Struts2Utils.renderJson(json);

        return null;
    } 
    /**
     * 删除用户组前，把该用户组从所有引用该用户组的用户中移除
     * @param ids
     */
    @Override
    public void prepareForDelete(Integer[] ids){
        User loginUser=UserHolder.getCurrentLoginUser();
        for(int id :ids){
            UserGroup userGroup=service.retrieve(UserGroup.class, id);
            boolean canDel=true;
            //获取拥有等待删除的角色的所有用户
            List<User> users=userGroup.getUsers();
            for(User user : users){
                if(loginUser.getId()==user.getId()){
                    canDel=false;
                }
            }
            if(!canDel) {
                continue;
            }
            for(User user : users){
                user.removeUserGroup(userGroup);
                service.update(user);
            }
        }
    }

    @Override
    public void assemblyModelForCreate(UserGroup model) {
        model.setRoles(roles);
    }

    @Override
    public void assemblyModelForUpdate(UserGroup model){
        //默认roles==null
        //当在修改用户组的时候，如果客户端不修改roles，则roles==null
        if(roles!=null){
            model.setRoles(roles);
        }
    }
    @Override
    protected void retrieveAfterRender(Map map,UserGroup model){
        map.put("roles", model.getRoleStrs());
    }
    public void setRoles(String roleStr) {
        String[] ids=roleStr.split(",");
        roles=new ArrayList<>();
        for(String id :ids){
            String[] attr=id.split("-");
            if(attr.length==2){
                if("role".equals(attr[0])){
                    Role role=service.retrieve(Role.class, Integer.parseInt(attr[1]));
                    roles.add(role);
                }
            }
        }   
    }    
}