package com.apdplat.module.system.service;

import com.apdplat.platform.log.APDPlatLogger;
import java.util.Locale;
import java.util.Properties;
import org.springframework.core.io.ClassPathResource;

public class PropertyHolder {
    protected static final APDPlatLogger log = new APDPlatLogger(PropertyHolder.class);
    private static Properties props = new Properties();

    static {
        reload();
    }

    public static Properties getProperties() {
        return props;
    }

    public static void reload() {
            String systemConfig="/com/apdplat/config.properties";
            String localConfig="/config.local.properties";
            String dbConfig="/com/apdplat/db.properties";
            String localDBConfig="/db.local.properties";
            ClassPathResource cr = null;
            try{
                cr = new ClassPathResource(systemConfig);
                props.load(cr.getInputStream());
                log.info("装入主配置文件:"+systemConfig);
                log.info("Main profile is loaded: "+systemConfig, Locale.ENGLISH);
            }catch(Exception e){
                log.info("装入主配置文件"+systemConfig+"失败!", e);
                log.info("Failed to load main profile "+systemConfig+"!", e, Locale.ENGLISH);
            }
            try{
                cr = new ClassPathResource(localConfig);
                props.load(cr.getInputStream());
                log.info("装入自定义主配置文件："+localConfig);
                log.info("Custom main profile is loaded: "+localConfig, Locale.ENGLISH);
            }catch(Exception e){
                log.info("装入自定义主配置文件"+localConfig+"失败！", e);
                log.info("Failed to load custom main profile "+localConfig+"！", e, Locale.ENGLISH);
            }            
            try{
                cr = new ClassPathResource(dbConfig);
                props.load(cr.getInputStream());
                log.info("装入数据库配置文件："+dbConfig);
                log.info("Database profile is loaded："+dbConfig);
            }catch(Exception e){
                log.info("装入数据库配置文件"+dbConfig+"失败！", e);
                log.info("Failed to load database profile "+dbConfig+"！", e, Locale.ENGLISH);
            }      
            try{  
                cr = new ClassPathResource(localDBConfig);
                props.load(cr.getInputStream());
                log.info("装入自定义数据库配置文件："+localDBConfig);
                log.info("Custom database profile is loaded："+localDBConfig, Locale.ENGLISH);
            }catch(Exception e){
                log.info("装入自定义数据库配置文件"+localDBConfig+"失败！",e);
                log.info("Failed to load custom database profile "+localDBConfig+"！", e, Locale.ENGLISH);
            }      
            
            String extendPropertyFiles = props.getProperty("extend.property.files");
            if(extendPropertyFiles!=null && !"".equals(extendPropertyFiles.trim())){
                String[] files=extendPropertyFiles.trim().split(",");
                for(String file : files){
                    try{  
                        cr = new ClassPathResource(file);
                        props.load(cr.getInputStream());
                        log.info("装入扩展配置文件："+file);
                        log.info("Extend profile is loaded："+file, Locale.ENGLISH);
                    }catch(Exception e){
                        log.info("装入扩展配置文件"+file+"失败！",e);
                        log.info("Failed to load extend profile"+file+"失败！",e, Locale.ENGLISH);
                    }      
                }
            }    
            log.info("系统配置属性装载完毕");
            log.info("System configuration properties finished loading", Locale.ENGLISH);
            log.info("******************属性列表***************************");
            log.info("******************Properties List********************", Locale.ENGLISH);
            for(String propertyName : props.stringPropertyNames()){
                log.info("  "+propertyName+" = "+props.getProperty(propertyName));
            }
            log.info("***********************************************************");
            
            //指定日志输出语言
            APDPlatLogger.setConfigLanguage(getLogLanguage());
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
