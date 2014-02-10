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

package org.apdplat.module.monitor.aspect;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Locale;
import org.apdplat.module.monitor.model.IndexLog;
import org.apdplat.module.monitor.model.IndexLogResult;
import org.apdplat.module.security.model.User;
import org.apdplat.module.security.service.UserHolder;
import org.apdplat.module.system.service.PropertyHolder;
import org.apdplat.module.system.service.SystemListener;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.apdplat.platform.log.BufferLogCollector;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Service;

/**
 * 重建索引日志Aspect
 * org.apdplat.platform.search.IndexManager类的
 * rebuidIndex方法负责重建索引
 * @author 杨尚川
 */
@Aspect
@Service
public class IndexLogAspect {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(IndexLogAspect.class);
    private static final boolean MONITOR_INDEX = PropertyHolder.getBooleanProperty("monitor.index");
    private IndexLog indexLog = null;
    
    static{
        if(MONITOR_INDEX){
            LOG.info("启用重建索引日志");
            LOG.info("Enable rebuild index log", Locale.ENGLISH);
        }else{
            LOG.info("禁用重建索引日志");
            LOG.info("Disable rebuild index log", Locale.ENGLISH);
        }
    }
    
    //拦截重建索引操作    
    @Pointcut("execution( boolean org.apdplat.platform.search.IndexRebuilder.build() )")
    public void rebuidIndex() {}
    
    @Before("rebuidIndex()")
    public void beforeRebuidIndex(JoinPoint jp) {
        if(MONITOR_INDEX){
            before();
        }
    }
    
    @AfterReturning(value="rebuidIndex()", argNames="result", returning = "result")  
    public void afterRebuidIndex(JoinPoint jp, boolean result) {
        if(MONITOR_INDEX){
            after(result);
        }
    }
    
    private void before(){
        LOG.info("准备记录重建索引日志");
        User user=UserHolder.getCurrentLoginUser();
        String ip=UserHolder.getCurrentUserLoginIp();
        indexLog=new IndexLog();
        if(user != null){
            indexLog.setUsername(user.getUsername());
        }
        indexLog.setLoginIP(ip);
        try {
            indexLog.setServerIP(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            LOG.error("无法获取服务器IP地址", e);
            LOG.error("Can't get server's ip address", e, Locale.ENGLISH);
        }
        indexLog.setAppName(SystemListener.getContextPath());
        indexLog.setStartTime(new Date());
    }
    private void after(boolean result){
        if(result){
            indexLog.setOperatingResult(IndexLogResult.SUCCESS);
        }else{
            indexLog.setOperatingResult(IndexLogResult.FAIL);
        }
        indexLog.setEndTime(new Date());
        indexLog.setProcessTime(indexLog.getEndTime().getTime()-indexLog.getStartTime().getTime());
        //将日志加入内存缓冲区
        BufferLogCollector.collect(indexLog);
        LOG.info("记录完毕");
    }
}