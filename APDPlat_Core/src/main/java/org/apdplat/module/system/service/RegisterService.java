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

package org.apdplat.module.system.service;

import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.model.Model;
import org.apdplat.platform.model.ModelMetaData;
import org.apdplat.platform.result.Page;
import org.apdplat.platform.service.ServiceFacade;
import org.apdplat.platform.util.ReflectionUtils;
import java.util.List;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public abstract class RegisterService<T extends Model> implements ApplicationListener {
    protected final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(getClass());
    
    @Resource(name="serviceFacade")
    protected ServiceFacade serviceFacade;
    @Resource(name="entityManagerFactory")
    protected EntityManagerFactory entityManagerFactory;
    
    protected Class<T> modelClass;
    @Override
    public void onApplicationEvent(ApplicationEvent event){
        if(event instanceof ContextRefreshedEvent){
            this.modelClass = ReflectionUtils.getSuperClassGenricType(getClass());
            LOG.info("spring容器初始化完成, 开始检查 "+ModelMetaData.getMetaData(this.modelClass.getSimpleName()) +" 是否需要初始化数据");
            if(shouldRegister()){
                LOG.info("需要初始化 "+ModelMetaData.getMetaData(this.modelClass.getSimpleName()));
                openEntityManager();
                registe();
                closeEntityManager();
                registeSuccess();
            }else{
                LOG.info("不需要初始化 "+ModelMetaData.getMetaData(this.modelClass.getSimpleName()));
            }
        }
    }
    private void openEntityManager(){        
        EntityManager em = entityManagerFactory.createEntityManager();
        TransactionSynchronizationManager.bindResource(entityManagerFactory, new EntityManagerHolder(em));
        LOG.info("打开实体管理器");
    }
    private void closeEntityManager(){
        EntityManagerHolder emHolder = (EntityManagerHolder)TransactionSynchronizationManager.unbindResource(entityManagerFactory);
        LOG.info("关闭实体管理器");
        EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager());
    }
    protected void registeSuccess(){
        
    }
    protected List<T> getRegisteData(){
        return null;
    }
    protected abstract void registe();

    protected boolean shouldRegister() {
        Page<T> page=serviceFacade.query(modelClass);
        if(page.getTotalRecords()==0) {
            return true;
        }
        return false;
    }
}