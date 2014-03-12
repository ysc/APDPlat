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

import org.apdplat.module.security.service.password.PasswordEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.service.ServiceFacade;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.apdplat.module.security.model.Org;
import org.apdplat.module.security.model.Position;
import org.apdplat.module.security.model.Role;
import org.apdplat.module.security.model.User;
import org.apdplat.module.security.model.UserGroup;
import org.apdplat.module.security.service.password.PasswordInvalidException;
import org.apdplat.module.security.service.password.PasswordStrategyExecuter;
import org.apdplat.module.system.service.PropertyHolder;
import org.apdplat.platform.criteria.Operator;
import org.apdplat.platform.criteria.Property;
import org.apdplat.platform.criteria.PropertyCriteria;
import org.apdplat.platform.criteria.PropertyEditor;
import org.apdplat.platform.criteria.PropertyType;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.apdplat.platform.result.Page;
import org.springframework.stereotype.Service;

/**
 * 用户业务逻辑
 * @author 杨尚川
 */
@Service
public class UserService {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(UserService.class);
    
    @Resource(name="serviceFacade")
    private ServiceFacade serviceFacade;
    @Resource(name="onlineUserService")
    private OnlineUserService onlineUserService;
    @Resource(name="passwordStrategyExecuter")
    private PasswordStrategyExecuter passwordStrategyExecuter;
    @Resource(name="passwordEncoder")
    private PasswordEncoder passwordEncoder;

    /**
     * 分页获取在线用户
     * @param start 开始索引（包括）
     * @param len 页面大小
     * @param orgIdStr 组织机构ID
     * @param roleIdStr 角色ID
     * @return 
     */
    public Page<User> getOnlineUsers(int start, int len, String orgIdStr, String roleIdStr){
        int orgId = -1;
        int roleId = -1;
        if(StringUtils.isNotBlank(orgIdStr)){
            orgId = Integer.parseInt(orgIdStr);
        }
        if(StringUtils.isNotBlank(roleIdStr)){
            roleId = Integer.parseInt(roleIdStr);
        }
        return getOnlineUsers(start, len, orgId, roleId);
    }
    /**
     * 分页获取在线用户
     * @param start 开始索引（包括）
     * @param len 页面大小
     * @param orgId 组织机构ID
     * @param roleId 角色ID
     * @return 
     */
    public Page<User> getOnlineUsers(int start, int len, int orgId, int roleId){
        if(start < 0){
            start = 0;
        }
        if(len < 1){
            len = 10;
        }
        Org org = null;
        Role role = null;
        if(orgId > 0){
            //返回特定组织架构及其所有子机构的在线用户
            org = serviceFacade.retrieve(Org.class, orgId);
        }
        if(roleId > 0){
            //返回属于特定角色的在线用户
            role = serviceFacade.retrieve(Role.class, roleId);
        }
        //获取在线用户
        List<User> users = onlineUserService.getUsers(org, role);
        LOG.info("获取在线用户, start: "+start+", len: "+len);
        //构造当前页面对象
        Page<User> page=new Page<>();
        List<User> models = new ArrayList<>();
        if(users == null || users.isEmpty()){
            LOG.info("没有获取到任何在线用户");
            page.setTotalRecords(0);
        }else{
            LOG.info("在线用户的总数为： "+users.size());
            int end = start+len;
            if(end > users.size()){
                end = users.size();
            }
            for(int i = start; i < end; i++){
                models.add(users.get(i));
            }
            page.setModels(models);
            page.setTotalRecords(users.size());
        }
        return page;
    }
    /**
     * 修改用户密码
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return map，key分别是success和message
     */
    public Map<String, Object> modifyPassword(String oldPassword, String newPassword){
        Map<String, Object> result = new HashMap<>();
        String message;
        User user = UserHolder.getCurrentLoginUser();
        if(user == null){        
            message = "用户未登陆，不能修改密码";
            result.put("success", false);
            result.put("message", message);
            LOG.error(message);
            return result;
        }
        if(PropertyHolder.getBooleanProperty("demo")){
            if(user.getUsername().equals("admin")){
                message = "演示版本admin用户不能更改密码";
                result.put("success", false);
                result.put("message", message);
                LOG.error(message);
                return result;
            }
        }
        //先对用户的密码策略进行验证
        try{
            passwordStrategyExecuter.check(newPassword);
        }catch(PasswordInvalidException e){
            result.put("success", false);
            result.put("message", e.getMessage());
            LOG.error(e.getMessage());
            return result;
        }            
        oldPassword=passwordEncoder.encode(oldPassword.trim(),user);
        if(oldPassword.equals(user.getPassword())){
            user.setPassword(passwordEncoder.encode(newPassword.trim(),user));
            serviceFacade.update(user);
            message = "修改成功";
            result.put("success", true);
            result.put("message", message);
            LOG.info(message);
        }else{
            message = "修改失败，旧密码错误";
            result.put("success", false);
            result.put("message", message);
            LOG.error(message);
        }
        return result;
    }
    /**
     * 将指定的用户进行密码重置
     * @param ids 指定的用户列表
     * @param password 新的密码
     * @return 操作结果消息
     */
    public String reset(Integer[] ids, String password){
        String message;
        if(ids == null || ids.length == 0){
            message = "未指定为哪些用户重置密码";
            LOG.error(message);
            return message;
        }
        //先对用户的密码策略进行验证
        try{
            passwordStrategyExecuter.check(password);
        }catch(PasswordInvalidException e){    
            LOG.error(e.getMessage());
            return e.getMessage();
        }
        int success = 0;
        for(int id : ids){
            User user = serviceFacade.retrieve(User.class, id);
            if(user == null){
                LOG.error("ID为 "+id+" 的用户不存在，无法为其重置密码");
                continue;
            }
            if(PropertyHolder.getBooleanProperty("demo") && "admin".equals(user.getUsername())){
                LOG.error("演示版本不能重置admin用户的密码");
                continue;
            }
            //设置新密码
            user.setPassword(passwordEncoder.encode(password, user));
            //同步到数据库
            serviceFacade.update(user);
            success++;
        }
        message = "已经成功将 "+success+" 个用户的密码重置为 "+password;
        int fail = ids.length - success;
        if(fail > 0){
            message += " , "+fail+" 个用户的密码重置失败";
        }
        LOG.info(message);
        return message;
    }
    /**
     * 删除用户之前的检查
     * 抛出异常可取消删除操作
     * @param ids 待删除的ID列表
     */
    public void prepareForDelete(Integer[] ids){
        User loginUser=UserHolder.getCurrentLoginUser();
        String message;
        for(int id :ids){
            if(PropertyHolder.getBooleanProperty("demo")){
                User toDeleteUser = serviceFacade.retrieve(User.class, id);
                if(toDeleteUser.getUsername().equals("admin")){
                    message = "演示版本不能删除admin用户";
                    LOG.error(message);
                    throw new RuntimeException(message);
                }
            }
            if(loginUser.getId() == id){
                message = "用户不能删除自己";
                LOG.error(message);
                throw new RuntimeException(message);
            }
        }   
    }
    /**
     * 每一次查询用户的时候都是查询特定的机构及其所有子机构下的用户
     * @param propertyCriteria 
     * @param orgId 
     * @return 
     */
    public PropertyCriteria buildPropertyCriteria(PropertyCriteria propertyCriteria, int orgId){
        if(propertyCriteria == null){
            propertyCriteria = new PropertyCriteria();
        }
        //orgId==-1或orgId<0代表为根节点，不加过滤条件
        if(orgId > 0){
            Org org = serviceFacade.retrieve(Org.class, orgId);
            //获取orgId的所有子机构的ID
            List<Integer> orgIds = OrgService.getChildIds(org);
            //加上orgId
            orgIds.add(org.getId());
            
            PropertyEditor pe = new PropertyEditor("org.id", Operator.in, PropertyType.List, orgIds);
            propertyCriteria.addPropertyEditor(pe);
        }
        return propertyCriteria;
    }    
    /**
     * 模型合法性检查
     * @param model 模型
     */
    public void checkModel(User model){
        /* 取得用户 */
        PropertyCriteria propertyCriteria = new PropertyCriteria();
        propertyCriteria.addPropertyEditor(new PropertyEditor("username", Operator.eq, "String", model.getUsername()));
        Page<User> pape = serviceFacade.query(User.class, null, propertyCriteria);
        if(pape.getTotalRecords() > 0){
            String message = "用户名【"+model.getUsername()+"】已存在，请更换用户名";
            LOG.error(message);
            //加快内存释放
            pape.getModels().clear();
            throw new RuntimeException(message);
        }
    }
    /**
     * 修改用户部分属性 - 组装模型
     * @param properties 
     * @param user 
     */
    public void assemblyModelForPartUpdate(List<Property> properties, User user) {
        for(Property property : properties){
            if("password".equals(property.getName().trim())){
                //先对用户的密码策略进行验证
                try{
                    passwordStrategyExecuter.check(property.getValue().toString());
                }catch(PasswordInvalidException e){
                    throw new RuntimeException(e.getMessage());
                }
                property.setValue(passwordEncoder.encode(property.getValue().toString(),user));
                break;
            }
        }
    }
    /**
     * 修改用户 - 组装模型
     * @param model 模型
     * @param roles 角色，形如：role-1,role-2,role-3
     * @param positions 岗位，形如：position-1,position-2,position-3
     * @param userGroups 用户组，形如：userGrou-1,userGrou-2,userGrou-3
     */
    public void assemblyModelForUpdate(User model, String roles, String positions, String userGroups){
        if(StringUtils.isNotBlank(roles)){
            LOG.debug("清除角色并重新组装: " + roles);
            model.clearRole();
            assemblyRoles(model, roles);
        }
        if(StringUtils.isNotBlank(positions)){
            LOG.debug("清除岗位并重新组装: " + positions);
            model.clearPosition();
            assemblyPositions(model, positions);
        }
        if(StringUtils.isNotBlank(userGroups)){
            LOG.debug("清除用户组并重新组装: " + userGroups);
            model.clearUserGroup();
            assemblyUserGroups(model, userGroups);
        }        
    }
    /**
     * 新增用户 - 组装模型
     * @param model 模型
     * @param roles 角色，形如：role-1,role-2,role-3
     * @param positions 岗位，形如：position-1,position-2,position-3
     * @param userGroups 用户组，形如：userGrou-1,userGrou-2,userGrou-3
     */
    public void assemblyModelForCreate(User model, String roles, String positions, String userGroups) {
        //先对用户的密码策略进行验证
        try{
            passwordStrategyExecuter.check(model.getPassword());
        }catch(PasswordInvalidException e){
            throw new RuntimeException(e.getMessage());
        }
        LOG.debug("加密用户密码");
        model.setPassword(passwordEncoder.encode(model.getPassword(), model));
        LOG.debug("组装角色: " + roles);
        assemblyRoles(model, roles);
        LOG.debug("组装岗位: " + positions);
        assemblyPositions(model, positions);
        LOG.debug("组装用户组: " + userGroups);
        assemblyUserGroups(model, userGroups);
    }
    private void assemblyRoles(User model, String roles){
        if(StringUtils.isNotBlank(roles)){
            String[] roleIds = roles.trim().split(",");
            for(String id : roleIds){
                String[] attr = id.split("-");
                if(attr.length == 2){
                    int roleId = Integer.parseInt(attr[1]);
                    Role temp = serviceFacade.retrieve(Role.class, roleId);
                    if(temp != null){
                        model.addRole(temp);
                        LOG.debug("角色 " + roleId + " 解析成功");
                    }
                }
            }
        }
    }
    private void assemblyPositions(User model, String positions){
        if(StringUtils.isNotBlank(positions)){
            String[] positionIds = positions.trim().split(",");
            for(String id : positionIds){
                String[] attr = id.split("-");
                if(attr.length == 2){
                    int positionId = Integer.parseInt(attr[1]);
                    Position temp=serviceFacade.retrieve(Position.class, positionId);
                    if(temp != null){
                        model.addPosition(temp);
                        LOG.debug("岗位 " + positionId + " 解析成功");
                    }
                }
            }
        }
    }
    private void assemblyUserGroups(User model, String userGroups){
        if(StringUtils.isNotBlank(userGroups)){
            String[] userGroupIds = userGroups.trim().split(",");
            for(String id : userGroupIds){
                String[] attr = id.split("-");
                if(attr.length == 2){
                    int userGroupId = Integer.parseInt(attr[1]);
                    UserGroup temp = serviceFacade.retrieve(UserGroup.class, userGroupId);
                    if(temp != null){
                        model.addUserGroup(temp);
                        LOG.debug("用户组 " + userGroupId + " 解析成功");
                    }
                }
            }
        }
    }
    
}