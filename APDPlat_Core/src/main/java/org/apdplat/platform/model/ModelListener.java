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

package org.apdplat.platform.model;

import org.apdplat.module.log.model.OperateLog;
import org.apdplat.module.log.model.OperateLogType;
import org.apdplat.module.security.model.User;
import org.apdplat.module.security.service.UserHolder;
import org.apdplat.module.system.service.LogQueue;
import org.apdplat.module.system.service.PropertyHolder;
import org.apdplat.module.system.service.SystemListener;
import org.apdplat.platform.annotation.IgnoreBusinessLog;
import org.apdplat.platform.annotation.IgnoreUser;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.search.IndexManager;
import org.apdplat.platform.util.SpringContextUtils;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import org.compass.annotations.Searchable;
/**
 * 模型监听器
 * @author 杨尚川
 *
 */
public class ModelListener {
    protected static final APDPlatLogger log = new APDPlatLogger(ModelListener.class);
    
    private static IndexManager indexManager=null;
    private static final boolean create;
    private static final boolean delete;
    private static final boolean update;
    
    static{
        create=PropertyHolder.getBooleanProperty("log.create");
        delete=PropertyHolder.getBooleanProperty("log.delete");
        update=PropertyHolder.getBooleanProperty("log.update");
        if(create){
            log.info("启用添加数据日志(Enable add data log)");
        }else{
            log.info("禁用添加数据日志(Disable add data log)");
        }
        if(delete){
            log.info("启用删除数据日志(Enable delete data log)");
        }else{
            log.info("禁用删除数据日志(Disable delete data log)");
        }
        if(update){
            log.info("启用更新数据日志(Enable update data log)");
        }else{
            log.info("禁用更新数据日志(Disable update data log)");
        }
    }

    private boolean indexManagerUsable(){
        if(indexManager==null){
            indexManager=SpringContextUtils.getBean("indexManager");
            if(indexManager==null){
                log.info("实时索引不可用(Real-time index is not available)");
            }else{
                log.info("实时索引开启(Launch real-time index)");
            }
        }
        
        return indexManager!=null;
    }

    @PrePersist
    public void prePersist(Model model) {
        User user=UserHolder.getCurrentLoginUser();
        if(user!=null && model.getOwnerUser()==null && !model.getClass().isAnnotationPresent(IgnoreUser.class)){
            //设置数据的拥有者
            model.setOwnerUser(user);
        }
        //设置创建时间
        model.setCreateTime(new Date());
    }

    @PostPersist
    public void postPersist(Model model) {
        if(indexManagerUsable() && model.getClass().isAnnotationPresent(Searchable.class)){
            indexManager.createIndex(model);
        }
        if(create){
            saveLog(model,OperateLogType.ADD);
        }
    }
    private void saveLog(Model model, String type){
        if(!model.getClass().isAnnotationPresent(IgnoreBusinessLog.class)){
            User user=UserHolder.getCurrentLoginUser();
            String ip=UserHolder.getCurrentUserLoginIp();
            OperateLog operateLog=new OperateLog();
            operateLog.setOwnerUser(user);
            operateLog.setLoginIP(ip);
            try {
                operateLog.setServerIP(InetAddress.getLocalHost().getHostAddress());
            } catch (UnknownHostException ex) {
                ex.printStackTrace();
            }
            operateLog.setAppName(SystemListener.getContextPath());
            operateLog.setOperatingTime(new Date());
            operateLog.setOperatingType(type);
            operateLog.setOperatingModel(model.getMetaData());
            operateLog.setOperatingID(model.getId());
            LogQueue.addLog(operateLog);
        }
    }
    @PreRemove
    public void preRemove(Model model) {

    }

    @PostRemove
    public void postRemove(Model model) {
        if(indexManagerUsable() && model.getClass().isAnnotationPresent(Searchable.class)){
            indexManager.deleteIndex(model.getClass(), model.getId());
        }
        if(delete){
            saveLog(model,OperateLogType.DELETE);
        }
    }

    @PreUpdate
    public  void preUpdate(Model model) {
        //设置更新时间
        model.setUpdateTime(new Date());
    }

    @PostUpdate
    public void postUpdate(Model model) {
        if(indexManagerUsable() && model.getClass().isAnnotationPresent(Searchable.class)){
            indexManager.updateIndex(model.getClass(),model);
        }
        if(update){
            saveLog(model,OperateLogType.UPDATE);
        }
    }

    @PostLoad
    public void postLoad(Model model) {
    }
}