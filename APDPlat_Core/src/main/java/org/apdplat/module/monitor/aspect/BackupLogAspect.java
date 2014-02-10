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
import org.apdplat.module.monitor.model.BackupLog;
import org.apdplat.module.monitor.model.BackupLogResult;
import org.apdplat.module.monitor.model.BackupLogType;
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
 * 备份恢复数据库日志Aspect
 * org.apdplat.module.system.service.backup.impl包下面有多个数据库的备份恢复实现
 * 他们实现了BackupService接口的backup方法（备份数据库）和restore（恢复数据库）方法
 * @author 杨尚川
 */
@Aspect
@Service
public class BackupLogAspect {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(BackupLogAspect.class);
    private static final boolean MONITOR_BACKUP = PropertyHolder.getBooleanProperty("monitor.backup");
    private BackupLog backupLog = null;
    
    static{
        if(MONITOR_BACKUP){            
            LOG.info("启用备份恢复日志");
            LOG.info("Enable backup restore log", Locale.ENGLISH);
        }else{
            LOG.info("禁用备份恢复日志");
            LOG.info("Disable backup restore log", Locale.ENGLISH);
        }
    }
    
    //拦截备份数据库操作    
    @Pointcut("execution( boolean org.apdplat.module.system.service.backup.impl.*.backup() )")
    public void backup() {}
    
    @Before("backup()")
    public void beforeBackup(JoinPoint jp) {
        if(MONITOR_BACKUP){
            before(BackupLogType.BACKUP);
        }
    }   
    
    @AfterReturning(value="backup()", argNames="result", returning = "result")  
    public void afterBackup(JoinPoint jp, boolean result) {
        if(MONITOR_BACKUP){
            after(result);
        }
    }
    
    //拦截恢复数据库操作    
    @Before(value="execution( boolean org.apdplat.module.system.service.backup.impl.*.restore(java.lang.String) ) && args(date)",
            argNames="date")
    public void beforeRestore(JoinPoint jp, String date) {
        if(MONITOR_BACKUP){
            before(BackupLogType.RESTORE);
        }
    }       
    
    @AfterReturning(pointcut="execution( boolean org.apdplat.module.system.service.backup.impl.*.restore(java.lang.String) )", 
            returning = "result")  
    public void afterRestore(JoinPoint jp, boolean result) {
        if(MONITOR_BACKUP){
            after(result);
        }
    }
    
    private void before(String type){
        LOG.info("准备记录数据库"+type+"日志");
        User user=UserHolder.getCurrentLoginUser();
        String ip=UserHolder.getCurrentUserLoginIp();
        backupLog=new BackupLog();
        if(user != null){
            backupLog.setUsername(user.getUsername());
        }
        backupLog.setLoginIP(ip);
        try {
            backupLog.setServerIP(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            LOG.error("无法获取服务器IP地址", e);
            LOG.error("Can't get server's ip address", e, Locale.ENGLISH);
        }
        backupLog.setAppName(SystemListener.getContextPath());
        backupLog.setStartTime(new Date());
        backupLog.setOperatingType(type);
    }
    private void after(boolean result){
        if(result){
            backupLog.setOperatingResult(BackupLogResult.SUCCESS);
        }else{
            backupLog.setOperatingResult(BackupLogResult.FAIL);
        }
        backupLog.setEndTime(new Date());
        backupLog.setProcessTime(backupLog.getEndTime().getTime()-backupLog.getStartTime().getTime());
        //将日志加入内存缓冲区
        BufferLogCollector.collect(backupLog);
        LOG.info("记录完毕");
    }
}