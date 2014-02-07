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

package org.apdplat.platform.log.handler;

import java.util.List;
import java.util.Locale;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.apdplat.platform.model.Model;
import org.apdplat.platform.service.ServiceFacade;
import org.apdplat.platform.util.ConvertUtils;
import org.apdplat.platform.util.SpringContextUtils;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 日志处理
 * 将日志存入独立日志数据库（非业务数据库）
 * 
 * @author 杨尚川
 */
@Service
public class DatabaseLogHandler implements LogHandler{
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(DatabaseLogHandler.class);
    //使用日志数据库
    @Resource(name = "serviceFacadeForLog")
    private ServiceFacade serviceFacade;

    /**
     * 打开日志数据库em
     * @param entityManagerFactory 
     */
    private static void openEntityManagerForLog(EntityManagerFactory entityManagerFactory){        
        EntityManager em = entityManagerFactory.createEntityManager();
        TransactionSynchronizationManager.bindResource(entityManagerFactory, new EntityManagerHolder(em));
        LOG.info("打开ForLog实体管理器");
    }
    /**
     * 关闭日志数据库em
     * @param entityManagerFactory 
     */
    private static void closeEntityManagerForLog(EntityManagerFactory entityManagerFactory){
        EntityManagerHolder emHolder = (EntityManagerHolder)TransactionSynchronizationManager.unbindResource(entityManagerFactory);
        LOG.info("关闭ForLog实体管理器");
        EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager());
    }
    @Override
    public <T extends Model> void handle(List<T> list) {
        int len = list.size();
        LOG.info("需要保存的日志数目："+len);
        LOG.info("The number of logs to be saved："+len, Locale.ENGLISH);
        long start=System.currentTimeMillis();
        EntityManagerFactory entityManagerFactoryForLog = SpringContextUtils.getBean("entityManagerFactoryForLog");
        openEntityManagerForLog(entityManagerFactoryForLog);
        //保存日志
        serviceFacade.create(list);
        closeEntityManagerForLog(entityManagerFactoryForLog);
        long cost=System.currentTimeMillis()-start;
        LOG.info("成功保存 "+len+" 条日志, 耗时: "+ConvertUtils.getTimeDes(cost));
        LOG.info("Success to save "+len+" logs, elapsed: "+ConvertUtils.getTimeDes(cost), Locale.ENGLISH);
    }
}
