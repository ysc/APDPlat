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

package org.apdplat.module.security.action;

import org.apdplat.module.security.model.Role;
import org.apdplat.module.security.model.User;
import org.apdplat.module.security.model.UserGroup;
import org.apdplat.module.security.service.UserGroupService;
import org.apdplat.module.security.service.UserHolder;
import org.apdplat.platform.action.ExtJSSimpleAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

@Scope("prototype")
@Controller
@RequestMapping("/security/user-group/")
public class UserGroupAction extends ExtJSSimpleAction<UserGroup> {
    @Resource
    private UserGroupService userGroupService;
    private List<Role> roles = null;

    @ResponseBody
    @RequestMapping("store.action")
    public String store(){     
        String json = userGroupService.toAllUserGroupJson();
        return json;
    }
    /**
     * 删除用户组前，把该用户组从所有引用该用户组的用户中移除
     * @param ids
     */
    @Override
    public void prepareForDelete(Integer[] ids){
        User loginUser=UserHolder.getCurrentLoginUser();
        for(int id :ids){
            UserGroup userGroup=getService().retrieve(UserGroup.class, id);
            boolean canDel=true;
            //获取拥有等待删除的角色的所有用户
            List<User> users=userGroup.getUsers();
            for(User user : users){
                if(loginUser.getId().intValue()==user.getId().intValue()){
                    canDel=false;
                }
            }
            if(!canDel) {
                continue;
            }
            users.forEach(user -> {
                user.removeUserGroup(userGroup);
                getService().update(user);
            });
        }
    }

    @Override
    public void assemblyModelForCreate(UserGroup model) {
        String roleStr = getRequest().getParameter("roles");
        LOG.debug("roles:" + roleStr);
        setRoles(roleStr);
        model.clearRoles();
        roles.forEach(role -> model.addRole(role));
    }

    @Override
    public void assemblyModelForUpdate(UserGroup model){
        //默认roles==null
        //当在修改用户组的时候，如果客户端不修改roles，则roles==null
        String roleStr = getRequest().getParameter("roles");
        LOG.debug("roles:"+roleStr);
        setRoles(roleStr);
        if(roles!=null){
            model.clearRoles();
            roles.forEach(role->model.addRole(role));
        }
    }
    @Override
    protected void retrieveAfterRender(Map map,UserGroup model){
        map.put("roles", model.getRoleStrs());
    }
    public void setRoles(String roleStr) {
        if(roleStr==null){
            return;
        }
        String[] ids=roleStr.split(",");
        roles=new ArrayList<>();
        for(String id :ids){
            String[] attr=id.split("-");
            if(attr.length==2){
                if("role".equals(attr[0])){
                    Role role=getService().retrieve(Role.class, Integer.parseInt(attr[1]));
                    roles.add(role);
                }
            }
        }   
    }    
}