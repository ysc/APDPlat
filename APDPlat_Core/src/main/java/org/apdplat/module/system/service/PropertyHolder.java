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
import java.util.Locale;
import java.util.Properties;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.springframework.core.io.ClassPathResource;

public class PropertyHolder {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(PropertyHolder.class);
    private static final Properties props = new Properties();

    static {
        init();
    }

    public static Properties getProperties() {
        return props;
    }
    /**
     * 本方法中的日志只能输出中文，因为APDPlatLoggerImpl中默认指定输出中文
     * 只有配置项加载完毕，调用了指定日志输出语言方法LOG.setLocale(getLogLanguage())
     * 之后，配置的日志输出语言才会生效
     */
    private static void init() {
            String systemConfig="/org/apdplat/config.properties";
            String localConfig="/config.local.properties";
            String dbConfig="/org/apdplat/db.properties";
            String localDBConfig="/db.local.properties";
            ClassPathResource cr = null;
            try{
                cr = new ClassPathResource(systemConfig);
                props.load(cr.getInputStream());
                LOG.info("装入主配置文件:"+systemConfig);
            }catch(Exception e){
                LOG.info("装入主配置文件"+systemConfig+"失败!", e);
            }
            try{
                cr = new ClassPathResource(localConfig);
                props.load(cr.getInputStream());
                LOG.info("装入自定义主配置文件："+localConfig);
            }catch(Exception e){
                LOG.info("装入自定义主配置文件"+localConfig+"失败！", e);
            }            
            try{
                cr = new ClassPathResource(dbConfig);
                props.load(cr.getInputStream());
                LOG.info("装入数据库配置文件："+dbConfig);
                LOG.info("Database profile is loaded："+dbConfig);
            }catch(Exception e){
                LOG.info("装入数据库配置文件"+dbConfig+"失败！", e);
            }      
            try{  
                cr = new ClassPathResource(localDBConfig);
                props.load(cr.getInputStream());
                LOG.info("装入自定义数据库配置文件："+localDBConfig);
            }catch(Exception e){
                LOG.info("装入自定义数据库配置文件"+localDBConfig+"失败！",e);
            }      
            
            String extendPropertyFiles = props.getProperty("extend.property.files");
            if(extendPropertyFiles!=null && !"".equals(extendPropertyFiles.trim())){
                String[] files=extendPropertyFiles.trim().split(",");
                for(String file : files){
                    try{  
                        cr = new ClassPathResource(file);
                        props.load(cr.getInputStream());
                        LOG.info("装入扩展配置文件："+file);
                    }catch(Exception e){
                        LOG.info("装入扩展配置文件"+file+"失败！",e);
                    }      
                }
            }    
            LOG.info("系统配置属性装载完毕");
            LOG.info("******************属性列表***************************");
            for(String propertyName : props.stringPropertyNames()){
                LOG.info("  "+propertyName+" = "+props.getProperty(propertyName));
            }
            LOG.info("***********************************************************");
            
            //指定日志输出语言
            LOG.setLocale(getLogLanguage());
    }
    /**
     * 日志使用什么语言输出
     * @return 
     */
    public static Locale getLogLanguage(){
       String language = getProperty("log.locale.language");
       return Locale.forLanguageTag(language);
    }

    public static boolean getBooleanProperty(String name) {
        String value = props.getProperty(name);

        return "true".equals(value);
    }

    public static int getIntProperty(String name) {
        String value = props.getProperty(name);

        return Integer.parseInt(value);
    }

    public static String getProperty(String name) {
        String value = props.getProperty(name);

        return value;
    }

    public static void setProperty(String name, String value) {
        props.setProperty(name, value);
    }
}