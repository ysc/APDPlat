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

package org.apdplat.platform.generator;

import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.model.Model;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

/**
 *
 * @author 杨尚川
 */
public abstract class Generator {
    protected static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(Generator.class);
    
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
            LOG.info("生成的文件为(Generated file is)："+file.getAbsolutePath());
        } catch (IOException e) {
            LOG.error("生成数据字典出错(Error in generate data dictionary)",e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    LOG.info(e.getMessage());
                }
            }
        }
    }
}