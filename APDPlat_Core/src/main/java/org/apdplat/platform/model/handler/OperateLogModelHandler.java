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

package org.apdplat.platform.model.handler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Locale;
import javax.annotation.PostConstruct;
import org.apdplat.module.log.model.OperateLog;
import org.apdplat.module.log.model.OperateLogType;
import org.apdplat.module.security.model.User;
import org.apdplat.module.security.service.UserHolder;
import org.apdplat.module.system.service.PropertyHolder;
import org.apdplat.module.system.service.SystemListener;
import org.apdplat.platform.annotation.IgnoreBusinessLog;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.apdplat.platform.log.BufferLogCollector;
import org.apdplat.platform.model.Model;
import org.apdplat.platform.model.ModelListener;
import org.springframework.stereotype.Service;

/**
 * 记录业务操作日志模型事件处理器
 * @author 杨尚川
 */
@Service
public class OperateLogModelHandler extends ModelHandler{
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(OperateLogModelHandler.class);

    private static final boolean CREATE;
    private static final boolean DELETE;
    private static final boolean UPDATE;
    
    static{
        CREATE=PropertyHolder.getBooleanProperty("log.create");
        DELETE=PropertyHolder.getBooleanProperty("log.delete");
        UPDATE=PropertyHolder.getBooleanProperty("log.update");
        if(CREATE){
            LOG.info("启用添加数据日志");
            LOG.info("Enable create data log", Locale.ENGLISH);
        }else{
            LOG.info("禁用添加数据日志");
            LOG.info("Disable create data log", Locale.ENGLISH);
        }
        if(DELETE){
            LOG.info("启用删除数据日志");
            LOG.info("Enable delete data log", Locale.ENGLISH);
        }else{
            LOG.info("禁用删除数据日志");
            LOG.info("Disable delete data log", Locale.ENGLISH);
        }
        if(UPDATE){
            LOG.info("启用更新数据日志");
            LOG.info("Enable update data log", Locale.ENGLISH);
        }else{
            LOG.info("禁用更新数据日志");
            LOG.info("Disable update data log", Locale.ENGLISH);
        }
    }
    
    /**
     * 注册模型处理器
     */
    @PostConstruct
    public void init(){
        ModelListener.addModelHandler(this);
    }
    
    @Override
    public void postPersist(Model model) {
        if(CREATE){
            saveLog(model,OperateLogType.ADD);
            LOG.debug("记录模型创建日志: "+model);
        }
    }
    
    @Override
    public void postRemove(Model model) {
        if(DELETE){
            saveLog(model,OperateLogType.DELETE);
            LOG.debug("记录模型删除日志: "+model);
        }
    }
    
    @Override
    public void postUpdate(Model model) {
        if(UPDATE){
            saveLog(model,OperateLogType.UPDATE);
            LOG.debug("记录模型修改日志: "+model);
        }
    }
    
    /**
     * 将日志加入BufferLogCollector定义的内存缓冲区
     * @param model
     * @param type 
     */
    private void saveLog(Model model, String type){
        //判断模型是否已经指定忽略记录增删改日志
        if(!model.getClass().isAnnotationPresent(IgnoreBusinessLog.class)){
            User user=UserHolder.getCurrentLoginUser();
            String ip=UserHolder.getCurrentUserLoginIp();
            OperateLog operateLog=new OperateLog();
            if(user != null){
                operateLog.setUsername(user.getUsername());
            }
            operateLog.setLoginIP(ip);
            try {
                operateLog.setServerIP(InetAddress.getLocalHost().getHostAddress());
            } catch (UnknownHostException ex) {
                LOG.error("无法获取服务器IP", ex);
            }
            operateLog.setAppName(SystemListener.getContextPath());
            operateLog.setOperatingTime(new Date());
            operateLog.setOperatingType(type);
            operateLog.setOperatingModel(model.getMetaData());
            operateLog.setOperatingID(model.getId());
            //将日志加入内存缓冲区
            BufferLogCollector.collect(operateLog);
        }
    }
}
