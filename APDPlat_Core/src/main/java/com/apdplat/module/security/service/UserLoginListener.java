package com.apdplat.module.security.service;

import com.apdplat.module.monitor.model.UserLogin;
import com.apdplat.module.security.model.User;
import com.apdplat.module.system.service.LogQueue;
import com.apdplat.module.system.service.PropertyHolder;
import com.apdplat.module.system.service.SystemListener;
import com.apdplat.platform.log.APDPlatLogger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;


public class UserLoginListener implements HttpSessionAttributeListener,HttpSessionListener  {
    protected static final APDPlatLogger log = new APDPlatLogger(UserLoginListener.class);

    private static Map<String,UserLogin> logs=new HashMap<>();

    private static Map<String,HttpSession> sessions=new HashMap<>();
    private static final boolean loginMonitor;
    static{
        loginMonitor=PropertyHolder.getBooleanProperty("monitor.login");
        if(loginMonitor){
            log.info("启用用户登录注销日志");
        }else{
            log.info("禁用用户登录注销日志");
        }
    }

    @Override
    public void attributeAdded(HttpSessionBindingEvent se) {
        if (se.getValue() instanceof SecurityContextImpl && loginMonitor) {
            User user=UserHolder.getCurrentLoginUser();
            if (null != user) {
                String sessioId=se.getSession().getId();
                log.info("用户 "+user.getUsername()+" 登录成功，会话ID："+sessioId);

                if(logs.get(user.getUsername())==null){
                    log.info("开始记录用户 "+user.getUsername()+" 的登录日志");
                    String ip=UserHolder.getCurrentUserLoginIp();
                    UserLogin userLogin=new UserLogin();
                    userLogin.setAppName(SystemListener.getContextPath());
                    userLogin.setLoginIP(ip);
                    userLogin.setUserAgent(se.getSession().getAttribute("userAgent").toString());
                    userLogin.setLoginTime(new Date());
                    try {
                        userLogin.setServerIP(InetAddress.getLocalHost().getHostAddress());
                    } catch (UnknownHostException e) {
                        log.error("记录登录日志出错",e);
                    }
                    userLogin.setOwnerUser(user);
                    logs.put(user.getUsername(), userLogin);
                }else{
                    log.info("用户 "+user.getUsername()+" 的登录日志已经被记录过，用户在未注销前又再次登录，忽略此登录");
                }
            }else{
                log.info("在登录的时候获得User失败");
            }
        }
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent se) {
        //不能从UserHolder中获取用户，因为会话已经销毁
        if (se.getValue() instanceof SecurityContextImpl && loginMonitor) {
            SecurityContext context=(SecurityContext)se.getValue();
            Authentication authentication=context.getAuthentication();
            if (authentication != null) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof User) {
                    User user = (User) principal;
                    if (null != user) {
                        String sessioId=se.getSession().getId();
                        log.info("用户 "+user.getUsername()+" 注销成功，会话ID："+sessioId);

                        UserLogin userLogin=logs.get(user.getUsername());
                        if(userLogin!=null){
                            log.info("开始记录用户 "+user.getUsername()+" 的注销日志");
                            userLogin.setLogoutTime(new Date());
                            userLogin.setOnlineTime(userLogin.getLogoutTime().getTime()-userLogin.getLoginTime().getTime());
                            LogQueue.addLog(userLogin);
                            logs.remove(user.getUsername());
                        }else{
                            log.info("无法记录用户 "+user.getUsername()+" 的注销日志，因为用户的登录日志不存在");
                        }
                    }else{
                        log.info("在注销的时候获得User失败");
                    }
                }
            }else{
                log.info("在注销的时候获得Authentication失败");
            }
        }
    }
    
    @Override
    public void attributeReplaced(HttpSessionBindingEvent se) {
    }

    @Override
    public void sessionCreated(HttpSessionEvent hse) {
        HttpSession session = hse.getSession();
        sessions.put(session.getId(), session);
        log.info("创建会话，ID："+session.getId()+" ,当前共有会话："+sessions.size());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent hse) {
        HttpSession session = hse.getSession();
        sessions.remove(session.getId());
        log.info("销毁会话，ID："+session.getId()+" ,当前共有会话："+sessions.size());
    }
    
    public static void forceAllUserOffline(){
        if(!loginMonitor){
            return;
        }
        int len=logs.size();
        if(len<1){
            return;
        }
        
        log.info("有 "+len+" 个用户还没有注销，强制所有用户退出");
        for(String username : logs.keySet()){
            UserLogin userLogin=logs.get(username);
            log.info("开始记录用户 "+username+" 的注销日志");
            userLogin.setLogoutTime(new Date());
            userLogin.setOnlineTime(userLogin.getLogoutTime().getTime()-userLogin.getLoginTime().getTime());
            LogQueue.addLog(userLogin);
            logs.remove(username);
        }
    }
}
