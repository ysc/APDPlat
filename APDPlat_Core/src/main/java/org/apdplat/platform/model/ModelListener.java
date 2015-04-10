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

import org.apdplat.platform.model.handler.ModelHandler;
import java.util.LinkedList;
import java.util.List;
import org.apdplat.platform.log.APDPlatLogger;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import org.apdplat.platform.log.APDPlatLoggerFactory;
/**
 * 模型监听事件调度器
 * 可注册与反注册多个ModelHandler的实现
 * 相应事件发生的时候，改调度器负责转发给所有注册的ModelHandler
 * @author 杨尚川
 *
 */
public class ModelListener {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(ModelListener.class);
    private static final List<ModelHandler> modelHandlers = new LinkedList<>();
    
    public static void addModelHandler(ModelHandler modelHandler){
        LOG.info("注册模型事件处理器："+modelHandler.getClass().getName());
        modelHandlers.add(modelHandler);
    }
    public static void removeModelHandler(ModelHandler modelHandler){
        LOG.info("移除模型事件处理器："+modelHandler.getClass().getName());
        modelHandlers.remove(modelHandler);
    }
    
    @PrePersist
    public void prePersist(Model model) {
        modelHandlers.forEach(modelHandler -> {
            modelHandler.prePersist(model);
        });
    }
    @PostPersist
    public void postPersist(Model model) {
        modelHandlers.forEach(modelHandler -> {
            modelHandler.postPersist(model);
        });
    }
    @PreRemove
    public void preRemove(Model model) {
        modelHandlers.forEach(modelHandler -> {
            modelHandler.preRemove(model);
        });
    }
    @PostRemove
    public void postRemove(Model model) {
        modelHandlers.forEach(modelHandler -> {
            modelHandler.postRemove(model);
        });
    }
    @PreUpdate
    public  void preUpdate(Model model) {
        modelHandlers.forEach(modelHandler -> {
            modelHandler.preUpdate(model);
        });
    }
    @PostUpdate
    public void postUpdate(Model model) {
        modelHandlers.forEach(modelHandler -> {
            modelHandler.postUpdate(model);
        });
    }
    @PostLoad
    public void postLoad(Model model) {
        modelHandlers.forEach(modelHandler -> {
            modelHandler.postLoad(model);
        });
    }
}