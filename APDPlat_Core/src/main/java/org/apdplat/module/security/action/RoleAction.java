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

import org.apdplat.module.module.model.Command;
import org.apdplat.module.security.model.Role;
import org.apdplat.module.security.model.User;
import org.apdplat.module.security.service.RoleService;
import org.apdplat.module.security.service.UserHolder;
import org.apdplat.module.system.service.PropertyHolder;
import org.apdplat.platform.action.ExtJSSimpleAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Scope("prototype")
@Controller
@RequestMapping("/security/role/")
public class RoleAction extends ExtJSSimpleAction<Role> {
    @Resource
    private RoleService roleService;
    private List<Command> commands;

    @ResponseBody
    @RequestMapping("store.action")
    public String store(@RequestParam(required=false) String node,
                        @RequestParam(required=false) String recursion){
        if(node==null){
            node = "root";
        }
        boolean r = "true".equals(recursion);
        if(node.trim().startsWith("root")){
            int rootId = roleService.getRootRole().getId();
            String json = roleService.toJson(rootId, r);
            return json;
        }else{
            String[] attr=node.trim().split("-");
            if(attr.length==2){
                int roleId=Integer.parseInt(attr[1]);
                String json=roleService.toJson(roleId,r);
                return json;
            }   
        }
        return "[]";
    }
    @Override
    protected void old(Role model) {
        if(PropertyHolder.getBooleanProperty("demo")){
            if(model.isSuperManager()){
                throw new RuntimeException("演示版本不能修改具有超级管理员权限的角色");
            }
        }
    }
    
    /**
     * 删除角色前，把该角色从所有引用该角色的用户中移除
     * @param ids
     */
    @Override
    public void prepareForDelete(Integer[] ids){
        User loginUser=UserHolder.getCurrentLoginUser();
        for(int id :ids){
            Role role=getService().retrieve(Role.class, id);            
            boolean canDel=true;
            //获取拥有等待删除的角色的所有用户
            List<User> users=role.getUsers();
            for(User user : users){
                if(PropertyHolder.getBooleanProperty("demo")){
                    if(user.getUsername().equals("admin")){
                        throw new RuntimeException("演示版本不能删除admin用户拥有的角色");
                    }
                }
                if(loginUser.getId().intValue()==user.getId().intValue()){
                    canDel=false;
                }
            }
            if(!canDel) {
                continue;
            }
            users.forEach(user -> {
                user.removeRole(role);
                getService().update(user);
            });
        }
    }

    @Override
    public void assemblyModelForCreate(Role model) {
        if(model.isSuperManager()){
            return;
        }
        String privileges = getRequest().getParameter("privileges");
        LOG.debug("privileges:"+privileges);
        setPrivileges(privileges);
        model.setCommands(commands);
    }

    @Override
    public void assemblyModelForUpdate(Role model){
        if(model.isSuperManager()){
            model.clearCommand();
            return;
        }
        //默认commands==null
        //当在修改角色的时候，如果客户端不修改commands，则commands==null
        String privileges = getRequest().getParameter("privileges");
        LOG.debug("privileges:"+privileges);
        setPrivileges(privileges);
        if(commands!=null){
            model.setCommands(commands);
        }
    }
    @Override
    protected void retrieveAfterRender(Map map,Role model){
        map.put("privileges", model.getModuleCommandStr());
        map.put("superManager", model.isSuperManager());
    }
    public void setPrivileges(String privileges) {
        if(privileges==null){
            return;
        }
        String[] ids=privileges.split(",");
        commands=new ArrayList<>();
        for(String id :ids){
            String[] attr=id.split("-");
            if(attr.length==2){
                if("command".equals(attr[0])){
                    Command command=getService().retrieve(Command.class, Integer.parseInt(attr[1]));
                    commands.add(command);
                }
            }
        }        
    }
}