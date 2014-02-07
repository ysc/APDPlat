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

import java.util.Date;
import javax.annotation.PostConstruct;
import org.apdplat.module.security.model.User;
import org.apdplat.module.security.service.UserHolder;
import org.apdplat.platform.annotation.IgnoreUser;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.apdplat.platform.model.Model;
import org.apdplat.platform.model.ModelListener;
import org.apdplat.platform.model.SimpleModel;
import org.springframework.stereotype.Service;

/**
 * 辅助模型处理器
 * @author 杨尚川
 */
@Service
public class AidModelHandler extends ModelHandler{
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(AidModelHandler.class);

    /**
     * 注册模型处理器
     */
    @PostConstruct
    public void init(){
        ModelListener.addModelHandler(this);
    }    
    /**
     * 设置数据的拥有者
     * 设置创建时间
     * @param model 
     */
    @Override
    public void prePersist(Model model) {        
        User user=UserHolder.getCurrentLoginUser();
        if(model instanceof SimpleModel){
            SimpleModel simpleModel = (SimpleModel)model;
            if(user!=null && simpleModel.getOwnerUser()==null && !model.getClass().isAnnotationPresent(IgnoreUser.class)){
                //设置数据的拥有者
                simpleModel.setOwnerUser(user);
                LOG.debug("设置模型"+model+"的拥有者为:"+user.getUsername());
            }
        }
        //设置创建时间
        model.setCreateTime(new Date());
        LOG.debug("设置模型"+model+"的创建时间");
    }
    /**
     * 设置更新时间
     * @param model 
     */
    @Override
    public void preUpdate(Model model) {
        //设置更新时间
        model.setUpdateTime(new Date());
        LOG.debug("设置模型"+model+"的更新时间");
    }
}
