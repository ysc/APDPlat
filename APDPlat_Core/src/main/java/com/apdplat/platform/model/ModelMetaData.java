package com.apdplat.platform.model;

import com.apdplat.platform.log.APDPlatLogger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ysc
 */
public class ModelMetaData {
    protected static final APDPlatLogger log = new APDPlatLogger(ModelMetaData.class);
    
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
        log.info("注册模型元数据(Register model metadata)，"+modelName+"="+model.getMetaData());
        des.put(modelName, model.getMetaData());
        metaData.put(modelName, model.getClass());
    }
    public static String getMetaData(String modelName){
        modelName=modelName.toLowerCase();
        String value = des.get(modelName);
        if(value==null){
           log.info("没有找到(Not find model metadata) "+modelName+" 的模型元数据"); 
           return "";
        }
        return value;
    }
}
