/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.apdplat.module.system.service;

import com.apdplat.platform.log.APDPlatLogger;
import com.apdplat.platform.model.Model;
import com.apdplat.platform.model.ModelMetaData;
import com.apdplat.platform.result.Page;
import com.apdplat.platform.service.ServiceFacade;
import com.apdplat.platform.util.ReflectionUtils;
import java.util.List;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public abstract class RegisterService<T extends Model> implements ApplicationListener {
    protected final APDPlatLogger log = new APDPlatLogger(getClass());
    
    @Resource(name="serviceFacade")
    protected ServiceFacade serviceFacade;
    @Resource(name="entityManagerFactory")
    protected EntityManagerFactory entityManagerFactory;
    
    protected Class<T> modelClass;
    @Override
    public void onApplicationEvent(ApplicationEvent event){
        if(event instanceof ContextRefreshedEvent){
            this.modelClass = ReflectionUtils.getSuperClassGenricType(getClass());
            log.info("spring容器初始化完成, 开始检查 "+ModelMetaData.getMetaData(this.modelClass.getSimpleName()) +" 是否需要初始化数据");
            if(shouldRegister()){
                log.info("需要初始化 "+ModelMetaData.getMetaData(this.modelClass.getSimpleName()));
                openEntityManager();
                registe();
                closeEntityManager();
                registeSuccess();
            }else{
                log.info("不需要初始化 "+ModelMetaData.getMetaData(this.modelClass.getSimpleName()));
            }
        }
    }
    private void openEntityManager(){        
        EntityManager em = entityManagerFactory.createEntityManager();
        TransactionSynchronizationManager.bindResource(entityManagerFactory, new EntityManagerHolder(em));
        log.info("打开实体管理器");
    }
    private void closeEntityManager(){
        EntityManagerHolder emHolder = (EntityManagerHolder)TransactionSynchronizationManager.unbindResource(entityManagerFactory);
        log.info("关闭实体管理器");
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