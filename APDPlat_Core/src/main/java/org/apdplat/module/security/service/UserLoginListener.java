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

import org.apdplat.module.monitor.model.UserLogin;
import org.apdplat.module.security.model.User;
import org.apdplat.module.system.service.LogQueue;
import org.apdplat.module.system.service.PropertyHolder;
import org.apdplat.module.system.service.SystemListener;
import org.apdplat.platform.log.APDPlatLogger;
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
    protected static final APDPlatLogger LOG = new APDPlatLogger(UserLoginListener.class);

    private static Map<String,UserLogin> LOGs=new HashMap<>();

    private static Map<String,HttpSession> sessions=new HashMap<>();
    private static final boolean LOGinMonitor;
    static{
        LOGinMonitor=PropertyHolder.getBooleanProperty("monitor.LOGin");
        if(LOGinMonitor){
            LOG.info("启用用户登录注销日志");
        }else{
            LOG.info("禁用用户登录注销日志");
        }
    }

    @Override
    public void attributeAdded(HttpSessionBindingEvent se) {
        if (se.getValue() instanceof SecurityContextImpl && LOGinMonitor) {
            User user=UserHolder.getCurrentLoginUser();
            if (null != user) {
                String sessioId=se.getSession().getId();
                LOG.info("用户 "+user.getUsername()+" 登录成功，会话ID："+sessioId);

                if(LOGs.get(user.getUsername())==null){
                    LOG.info("开始记录用户 "+user.getUsername()+" 的登录日志");
                    String ip=UserHolder.getCurrentUserLoginIp();
                    UserLogin userLogin=new UserLogin();
                    userLogin.setAppName(SystemListener.getContextPath());
                    userLogin.setLoginIP(ip);
                    userLogin.setUserAgent(se.getSession().getAttribute("userAgent").toString());
                    userLogin.setLoginTime(new Date());
                    try {
                        userLogin.setServerIP(InetAddress.getLocalHost().getHostAddress());
                    } catch (UnknownHostException e) {
                        LOG.error("记录登录日志出错",e);
                    }
                    userLogin.setUsername(user.getUsername());
                    LOGs.put(user.getUsername(), userLogin);
                }else{
                    LOG.info("用户 "+user.getUsername()+" 的登录日志已经被记录过，用户在未注销前又再次登录，忽略此登录");
                }
            }else{
                LOG.info("在登录的时候获得User失败");
            }
        }
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent se) {
        //不能从UserHolder中获取用户，因为会话已经销毁
        if (se.getValue() instanceof SecurityContextImpl && LOGinMonitor) {
            SecurityContext context=(SecurityContext)se.getValue();
            Authentication authentication=context.getAuthentication();
            if (authentication != null) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof User) {
                    User user = (User) principal;
                    if (null != user) {
                        String sessioId=se.getSession().getId();
                        LOG.info("用户 "+user.getUsername()+" 注销成功，会话ID："+sessioId);

                        UserLogin userLogin=LOGs.get(user.getUsername());
                        if(userLogin!=null){
                            LOG.info("开始记录用户 "+user.getUsername()+" 的注销日志");
                            userLogin.setLogoutTime(new Date());
                            userLogin.setOnlineTime(userLogin.getLogoutTime().getTime()-userLogin.getLoginTime().getTime());
                            LogQueue.addLog(userLogin);
                            LOGs.remove(user.getUsername());
                        }else{
                            LOG.info("无法记录用户 "+user.getUsername()+" 的注销日志，因为用户的登录日志不存在");
                        }
                    }else{
                        LOG.info("在注销的时候获得User失败");
                    }
                }
            }else{
                LOG.info("在注销的时候获得Authentication失败");
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
        LOG.info("创建会话，ID："+session.getId()+" ,当前共有会话："+sessions.size());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent hse) {
        HttpSession session = hse.getSession();
        sessions.remove(session.getId());
        LOG.info("销毁会话，ID："+session.getId()+" ,当前共有会话："+sessions.size());
    }
    
    public static void forceAllUserOffline(){
        if(!LOGinMonitor){
            return;
        }
        int len=LOGs.size();
        if(len<1){
            return;
        }
        
        LOG.info("有 "+len+" 个用户还没有注销，强制所有用户退出");
        for(String username : LOGs.keySet()){
            UserLogin userLogin=LOGs.get(username);
            LOG.info("开始记录用户 "+username+" 的注销日志");
            userLogin.setLogoutTime(new Date());
            userLogin.setOnlineTime(userLogin.getLogoutTime().getTime()-userLogin.getLoginTime().getTime());
            LogQueue.addLog(userLogin);
            LOGs.remove(username);
        }
    }
}