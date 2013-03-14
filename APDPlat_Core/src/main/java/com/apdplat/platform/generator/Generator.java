package com.apdplat.platform.generator;

import com.apdplat.platform.log.APDPlatLogger;
import com.apdplat.platform.model.Model;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

/**
 *
 * @author ysc
 */
public abstract class Generator {
    protected static final APDPlatLogger log = new APDPlatLogger(Generator.class);
    
    protected static final String ENCODING = "utf-8";
    protected static final FreeMarkerConfigurationFactoryBean factory = new FreeMarkerConfigurationFactoryBean();
    protected static final Map<String,Model> actionToModel=new HashMap<>();  
    
    /**
     * 当Action和Model没有遵循约定，即Action为UserAction,Model为User这种方式时：
     * 给特定的Action指定特定的Model
     * 如：CanLendTipAction 对应 Sms
     * 则action为canLendTip，realModel为 sms
     * @param action
     * @param model 
     */
    public static <T extends Model> void setActionModelMap(List<String> actions,T model){
        for(String action : actions){
            actionToModel.put(action, model);
        }
    }
    protected static void saveFile(File file, String content) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));

            writer.write(content);
            writer.flush();
            log.info("生成的文件为(Generated file is)："+file.getAbsolutePath());
        } catch (IOException e) {
            log.error("生成数据字典出错(Error in generate data dictionary)",e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    log.info(e.getMessage());
                }
            }
        }
    }
}
