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

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.apdplat.platform.model.Model;
import org.apdplat.platform.model.ModelListener;
import org.apdplat.platform.search.IndexManager;
import org.apdplat.platform.search.annotations.Searchable;
import org.springframework.stereotype.Service;

/**
 * 实时索引模型处理器
 * @author 杨尚川
 */
@Service
public class RealtimeIndexModelHandler extends ModelHandler{
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(RealtimeIndexModelHandler.class);

    @Resource(name = "indexManager")
    private IndexManager indexManager;    
    
    /**
     * 注册模型处理器
     */
    @PostConstruct
    public void init(){
        ModelListener.addModelHandler(this);
    }
    
    @Override
    public void postPersist(Model model) {
        if(model.getClass().isAnnotationPresent(Searchable.class)){            
            indexManager.createIndex(model);
            LOG.debug("为模型："+model+" 实时创建索引，增加");
        }
    }
    
    @Override
    public void postRemove(Model model) {
        if(model.getClass().isAnnotationPresent(Searchable.class)){
            indexManager.deleteIndex(model.getClass(), model.getId());
            LOG.debug("为模型："+model+" 实时创建索引，删除");
        }
    }
    
    @Override
    public void postUpdate(Model model) {
        if(model.getClass().isAnnotationPresent(Searchable.class)){
            indexManager.updateIndex(model.getClass(),model);
            LOG.debug("为模型："+model+" 实时创建索引，修改");
        }
    }
}
