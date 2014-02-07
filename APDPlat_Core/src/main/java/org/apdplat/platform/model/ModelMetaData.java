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

import org.apdplat.platform.log.APDPlatLogger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apdplat.platform.log.APDPlatLoggerFactory;

/**
 *
 * @author 杨尚川
 */
public class ModelMetaData {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(ModelMetaData.class);
    
    private static Map<String,String> des=new HashMap<>();
    private static Map<String,Class<? extends Model>> metaData=new HashMap<>();
    public static Map<String,String> getModelDes(){
        return Collections.unmodifiableMap(des);
    }
    public static void addMetaData(Model model){
        String modelName=model.getClass().getSimpleName().toLowerCase();
        if(des.get(modelName)!=null){
            return ;
        }
        LOG.info("注册模型元数据(Register model metadata)，"+modelName+"="+model.getMetaData());
        des.put(modelName, model.getMetaData());
        metaData.put(modelName, model.getClass());
    }
    public static String getMetaData(String modelName){
        modelName=modelName.toLowerCase();
        String value = des.get(modelName);
        if(value==null){
           LOG.info("没有找到(Not find model metadata) "+modelName+" 的模型元数据"); 
           return "";
        }
        return value;
    }
}