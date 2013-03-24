package com.apdplat.module.security.action;

import com.apdplat.module.module.model.Command;
import com.apdplat.module.security.model.Role;
import com.apdplat.module.security.model.User;
import com.apdplat.module.security.service.RoleService;
import com.apdplat.module.security.service.UserHolder;
import com.apdplat.module.system.service.PropertyHolder;
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
public class RoleAction extends ExtJSSimpleAction<Role> {
    private String node;
    @Resource(name="roleService")
    private RoleService roleService;
    private List<Command> commands;
    private boolean recursion=false;

    public String store(){            
        if(recursion){
            int rootId = roleService.getRootRole().getId();
            String json=roleService.toJson(rootId,recursion);
            Struts2Utils.renderJson(json);

            return null;
        }

        return query();
    }
    
    @Override
    public String query(){
        //如果node为null则采用普通查询方式
        if(node==null){
            return super.query();
        }
        //如果指定了node则采用自定义的查询方式
        if(node.trim().startsWith("root")){
            String json=roleService.toRootJson(recursion);
            Struts2Utils.renderJson(json);
        }else{
            String[] attr=node.trim().split("-");
            if(attr.length==2){
                int roleId=Integer.parseInt(attr[1]);
                String json=roleService.toJson(roleId,recursion);
                Struts2Utils.renderJson(json);                    
            }   
        }
        return null;
    }
    
    /**
     * 删除角色前，把该角色从所有引用该角色的用户中移除
     * @param ids
     */
    @Override
    public void prepareForDelete(Integer[] ids){
        User loginUser=UserHolder.getCurrentLoginUser();
        for(int id :ids){
            Role role=service.retrieve(Role.class, id);            
            boolean canDel=true;
            //获取拥有等待删除的角色的所有用户
            List<User> users=role.getUsers();
            for(User user : users){                
                if(PropertyHolder.getBooleanProperty("demo")){
                    if(user.getUsername().equals("admin")){
                        throw new RuntimeException("演示版本不能删除admin用户拥有的角色");
                    }
                }
                if(loginUser.getId()==user.getId()){
                    canDel=false;
                }
            }
            if(!canDel) {
                continue;
            }
            for(User user : users){
                user.removeRole(role);
                service.update(user);
            }
        }
    }

    @Override
    public void assemblyModelForCreate(Role model) {
        if(model.isSuperManager()){
            return;
        }
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
        String[] ids=privileges.split(",");
        commands=new ArrayList<>();
        for(String id :ids){
            String[] attr=id.split("-");
            if(attr.length==2){
                if("command".equals(attr[0])){
                    Command command=service.retrieve(Command.class, Integer.parseInt(attr[1]));
                    commands.add(command);
                }
            }
        }        
    }

    public void setRecursion(boolean recursion) {
        this.recursion = recursion;
    }

    public void setNode(String node) {
        this.node = node;
    }
}
