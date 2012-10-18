package com.apdplat.module.security.action;

import com.apdplat.module.module.model.Command;
import com.apdplat.module.security.model.Role;
import com.apdplat.module.security.model.User;
import com.apdplat.module.security.service.RoleService;
import com.apdplat.module.security.service.UserHolder;
import com.apdplat.platform.action.ExtJSSimpleAction;
import com.apdplat.platform.util.Struts2Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Scope("prototype")
@Controller
@Namespace("/security")
public class RoleAction extends ExtJSSimpleAction<Role> {
    @Resource(name="roleService")
    private RoleService roleService;
    private List<Command> commands;
    private String userId;
    public String store(){
        String json="";
        if(StringUtils.isBlank(userId)){
            //返回系统中的所有角色列表
            json=roleService.toAllRoleJson();
        }else{
            //返回系统中的所有角色列表
            json=roleService.toUserRoleJson(userId);
        }
        Struts2Utils.renderJson(json);
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
    protected void renderJsonForRetrieve(Map map) {
        render(map,model);

        map.put("privileges", model.getModuleCommandStr());
        map.put("superManager", model.isSuperManager());
    }
    @Override
    protected void renderJsonForQuery(List result) {
        for (Role role : page.getModels()) {
            Map temp = new HashMap();
            render(temp,role);
            result.add(temp);
        }
    }
    @Override
    protected void render(Map map,Role model){
        map.put("id", model.getId());
        map.put("version", model.getVersion());
        map.put("roleName", model.getRoleName());
        map.put("des", model.getDes());
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

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
