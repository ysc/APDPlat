package com.apdplat.module.system.service;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

public class PropertyHolder {

    protected static final Logger log = LoggerFactory.getLogger(PropertyHolder.class);
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
                log.info("装入主配置文件："+systemConfig);
            }catch(Exception e){
                log.info("装入主配置文件"+systemConfig+"失败！");
            }
            try{
                cr = new ClassPathResource(localConfig);
                props.load(cr.getInputStream());
                log.info("装入自定义主配置文件："+localConfig);
            }catch(Exception e){
                log.info("装入自定义主配置文件"+localConfig+"失败！");
            }            
            try{
                cr = new ClassPathResource(dbConfig);
                props.load(cr.getInputStream());
                log.info("装入数据库配置文件："+dbConfig);
            }catch(Exception e){
                log.info("装入数据库配置文件"+dbConfig+"失败！");
            }      
            try{  
                cr = new ClassPathResource(localDBConfig);
                props.load(cr.getInputStream());
                log.info("装入自定义数据库配置文件："+localDBConfig);
            }catch(Exception e){
                log.info("装入自定义数据库配置文件"+localDBConfig+"失败！");
            }      
            
            String extendPropertyFiles = props.getProperty("extend.property.files");
            if(extendPropertyFiles!=null && !"".equals(extendPropertyFiles.trim())){
                String[] files=extendPropertyFiles.trim().split(",");
                for(String file : files){
                    try{  
                        cr = new ClassPathResource(file);
                        props.load(cr.getInputStream());
                        log.info("装入扩展配置文件："+file);
                    }catch(Exception e){
                        log.info("装入扩展配置文件"+file+"失败！");
                    }      
                }
            }    
            log.info("系统配置属性装载完毕");
            log.info("*******************属性列表********************************");
            for(String propertyName : props.stringPropertyNames()){
                log.info("  "+propertyName+" = "+props.getProperty(propertyName));
            }
            log.info("***********************************************************");
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
