package com.apdplat.module.security.service;

import com.apdplat.module.security.model.Org;
import com.apdplat.module.security.model.Role;
import com.apdplat.module.security.model.User;
import com.apdplat.platform.log.APDPlatLogger;
import com.apdplat.platform.util.SpringContextUtils;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;

public class OnlineUserService{
    protected static final APDPlatLogger log = new APDPlatLogger(OnlineUserService.class);

    private static SessionRegistry sessionRegistry;
    public static String getUsername(String sessionID) {
        User user = getUser(sessionID);
        if (user == null) {
            return "匿名用户";
        }
        return user.getUsername();
    }
    public static User getUser(String sessionID) {
        User user = null;
        if(sessionRegistry==null){
            sessionRegistry=SpringContextUtils.getBean("sessionRegistry");
        }
        if(sessionRegistry==null){
            log.debug("没有从spring中获取到sessionRegistry");
            return null;
        }
        SessionInformation info=sessionRegistry.getSessionInformation(sessionID);
        if(info==null){
            log.debug("没有获取到会话ID为："+sessionID+" 的在线用户");
            return null;
        }
        user = (User)info.getPrincipal();
        log.debug("获取到会话ID为："+sessionID+" 的在线用户");
        
        
        return user;
    }

    public static List<User> getUser(Org org,Role role){
        if(sessionRegistry==null){
            sessionRegistry=SpringContextUtils.getBean("sessionRegistry");
        }
        if(sessionRegistry==null){
            log.info("没有从spring中获取到sessionRegistry");
            return null;
        }
        List<Object> users=sessionRegistry.getAllPrincipals();
        List<User> result=new ArrayList<>();
        log.info("获取在线用户,org:"+org+",role:"+role);
        if(org==null && role==null ){
            //返回所有在线用户
            for(Object obj : users){
                User user=(User)obj;
                log.info("获取到会话ID为："+sessionRegistry.getAllSessions(obj, false).get(0).getSessionId() +" 的在线用户");
                result.add(user);
            }
        }
        //取交集
        if(org!=null && role!=null){
            //返回特定组织架构及其所有子机构 且 属于特定角色的在线用户
            List<Integer> orgIds=OrgService.getChildIds(org);
            orgIds.add(org.getId());
            List<Integer> roleIds=RoleService.getChildIds(role);
            roleIds.add(role.getId());
            log.info("特定组织架构及其所有子组织架构:"+orgIds);
            log.info("特定角色及其所有子角色:"+orgIds);
            for(Object obj : users){
                User user=(User)obj;
                log.info("获取到会话ID为："+sessionRegistry.getAllSessions(obj, false).get(0).getSessionId() +" 的在线用户");
                if(orgIds.contains(user.getOrg().getId())){
                    for(Role r : user.getRoles()){
                        if(roleIds.contains(r.getId())){
                            result.add(user);
                            break;
                        }
                    }
                }
            }
            return result;
        }
        if(org!=null){
            //返回特定组织架构及其所有子组织架构的在线用户
            List<Integer> ids=OrgService.getChildIds(org);
            ids.add(org.getId());
            log.info("特定组织架构及其所有子机构:"+ids);
            for(Object obj : users){
                User user=(User)obj;
                log.info("获取到会话ID为："+sessionRegistry.getAllSessions(obj, false).get(0).getSessionId() +" 的在线用户");
                if(ids.contains(user.getOrg().getId())){
                    result.add(user);
                }
            }
        }
        if(role!=null){
            //返回属于特定角色及其所有子角色的在线用户
            List<Integer> roleIds=RoleService.getChildIds(role);
            roleIds.add(role.getId());
            for(Object obj : users){
                User user=(User)obj;
                log.info("获取到会话ID为："+sessionRegistry.getAllSessions(obj, false).get(0).getSessionId() +" 的在线用户");
                for(Role r : user.getRoles()){
                    if(roleIds.contains(r.getId())){
                        result.add(user);
                        break;
                    }
                }
            }
        }
        return result;
    }
}
