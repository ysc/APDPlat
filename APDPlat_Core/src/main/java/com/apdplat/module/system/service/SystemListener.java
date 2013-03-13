package com.apdplat.module.system.service;

import com.apdplat.module.dictionary.generator.DictionaryGenerator;
import com.apdplat.module.monitor.model.RuningTime;
import com.apdplat.module.monitor.service.MemoryMonitorThread;
import com.apdplat.module.security.service.UserLoginListener;
import com.apdplat.platform.util.FileUtils;
import com.apdplat.platform.util.ZipUtils;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.Enumeration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 系统启动和关闭的监听器,由Spring来调用
 * @author 杨尚川
 *
 */
public class SystemListener{
    private static boolean running=false;
    
    private static String basePath;
    private static String contextPath;
    protected static final Logger log = LoggerFactory.getLogger(SystemListener.class);
    private static RuningTime runingTime=null;
    private static final  boolean memoryMonitor;
    private static final  boolean runingMonitor;
    private static MemoryMonitorThread memoryMonitorThread;
    private static final File tmpDir=new File(System.getProperty("java.io.tmpdir"));
    static{
        memoryMonitor=PropertyHolder.getBooleanProperty("monitor.memory");        
        if(memoryMonitor){
            log.info("启用内存监视日志(Enable memory monitor log)");
        }else{
            log.info("禁用内存监视日志(Disable memory monitor log)");
        }
        runingMonitor=PropertyHolder.getBooleanProperty("monitor.runing");
        if(runingMonitor){
            log.info("启用系统运行日志(Enable system log)");
        }else{
            log.info("禁用系统运行日志(Disable system log)");
        }
    }

    public static boolean isRunning() {
        return running;
    }
    
    public static void prepareForSpring(){
        //供spring扫描组件用
        String basePackage=PropertyHolder.getProperty("basePackages");
        String localBasePackage=PropertyHolder.getProperty("basePackages.local");
        if(localBasePackage!=null && !"".equals(localBasePackage.trim())){
            basePackage=basePackage+","+localBasePackage;
        }
        System.setProperty("basePackage", basePackage);        
    }
    public static void contextInitialized(ServletContextEvent sce) {
        try{
            org.apache.commons.io.FileUtils.deleteDirectory(tmpDir);
            log.info("成功清除临时目录(Successed to clean temp directory):"+tmpDir.getAbsolutePath());
        }catch(Exception e){
            log.info("清除临时目录失败(Failed to clean temp directory):"+tmpDir.getAbsolutePath());
        }
        if(!tmpDir.exists()){
            tmpDir.mkdirs();
        }
        contextPath=sce.getServletContext().getContextPath();
        log.info("启动(Launch)【"+PropertyHolder.getProperty("app.name")+"】");
        log.info("应用上下文(App context):"+contextPath);
        ServletContext sc=sce.getServletContext();
        basePath=sc.getRealPath("/");
        if(!basePath.endsWith(File.separator)){
            basePath=basePath+File.separator;
        }
        //整个系统中的文件操作都以basePath为基础
        FileUtils.setBasePath(basePath);
        log.info("basePath:"+basePath);
        String userDir = System.getProperty("user.dir");
        log.info("user.dir:"+userDir);
        userDir=FileUtils.getAbsolutePath("/WEB-INF/classes/data/");
        System.setProperty("user.dir", userDir);
        log.info("将user.dir重新设置为(Reset user directory):"+userDir);
        
        String encoding=System.getProperty("file.encoding"); 
        log.info("你的操作系统所用的编码file.encoding(Encoding of your OS is)："+encoding);
        
        //为spring的配置做预处理
        prepareForSpring();
        //注册模块
        registerModules();
        //解析所有的dic.xml文件，并生成供客户端EXT JS调用的文件
        DictionaryGenerator.generateDic(basePath);
        
        if(runingMonitor){
            log.info("记录服务器启动日志(Recording the server boot logging)");
            runingTime=new RuningTime();
            try {
                runingTime.setServerIP(InetAddress.getLocalHost().getHostAddress());
            } catch (UnknownHostException e) {
                log.error("记录服务器启动日志出错(Failed to record the server boot logging)",e);
            }
            runingTime.setAppName(contextPath);
            runingTime.setOsName(System.getProperty("os.name"));
            runingTime.setOsVersion(System.getProperty("os.version"));
            runingTime.setOsArch(System.getProperty("os.arch"));
            runingTime.setJvmName(System.getProperty("java.vm.name"));
            runingTime.setJvmVersion(System.getProperty("java.vm.version"));
            runingTime.setJvmVendor(System.getProperty("java.vm.vendor"));
            runingTime.setStartupTime(new Date());
        }
        if(memoryMonitor){
            log.info("启动内存监视线程(Enable memory monitor thread)");
            int circle=PropertyHolder.getIntProperty("monitor.memory.circle");
            memoryMonitorThread=new MemoryMonitorThread(circle);
            memoryMonitorThread.start();
        }
        running=true;
    }
    public static void contextDestroyed(ServletContextEvent sce) {
        UserLoginListener.forceAllUserOffline();
        
        if(runingMonitor){
            log.info("记录服务器关闭日志(Recording the server shutdown logging)");        
            runingTime.setShutdownTime(new Date());
            runingTime.setRuningTime(runingTime.getShutdownTime().getTime()-runingTime.getStartupTime().getTime());
            LogQueue.addLog(runingTime);
        }
        if(memoryMonitor){
            log.info("停止内存监视线程(Stop memory monitor thread)");
            memoryMonitorThread.running=false;
            memoryMonitorThread.interrupt();
        }
        
        if(LogQueue.getLogQueue()!=null){
                LogQueue.getLogQueue().saveLog();
        }
        deregisterDrivers();
        log.info("卸载JDBC驱动(Uninstalled JDBC driver)");
    }
    public static String getContextPath() {
        return contextPath;
    }

    private static void deregisterDrivers() {
        Enumeration<Driver> drivers=DriverManager.getDrivers();
        while(drivers.hasMoreElements()){
            Driver driver=drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
            } catch (SQLException e) {
                log.warn("卸载JDBC驱动失败(Fail to uninstall JDBC driver)："+driver,e);
            }
        }
    }
    private static void registerModules(){
        StringBuilder modules=new StringBuilder();
        try {
            Enumeration<URL> ps = Thread.currentThread().getContextClassLoader().getResources("META-INF/services/module.xml");
            while(ps.hasMoreElements()) {
                URL url=ps.nextElement();
                String file=url.getFile();
                if(file.contains(".jar!")){
                    int start=file.indexOf("WEB-INF/lib/");
                    int end=file.indexOf("!/META-INF/services/module.xml");
                    if(start==-1 || end==-1){
                        continue;
                    }
                    String jar=file.substring(start, end);
                    modules.append(jar).append(",");
                    log.info("注册模块(Register)："+jar);
                    extractWebFromModule(jar);
                    extractDataFromModule(jar);
                }else{
                    log.warn("在非jar包中找到(Find)META-INF/services/module.xml(in non-jar)");
                }
            }
        } catch (IOException e) {
            log.error("注册模块出错(Failed to register)",e);
        }
        if(modules.length()>0){
            modules=modules.deleteCharAt(modules.length()-1);
        }
        //从配置文件中获取属性
        String scanJars=PropertyHolder.getProperty("scan.jars");
        log.info("注册模块前(Before register)，scanJars: "+scanJars);
        if(scanJars!=null && !"".equals(scanJars.trim())){
            scanJars=scanJars+","+modules.toString();
        }else{
            scanJars=modules.toString();
        }
        //设置到系统属性中
        //spring会把系统属性中的配置覆盖掉配置文件中的配置
        //<property name="systemPropertiesModeName" value="SYSTEM_PROPERTIES_MODE_OVERRIDE" />
        System.setProperty("scan.jars", scanJars);
        //设置回配置属性
        PropertyHolder.setProperty("scan.jars", scanJars);
        log.info("注册模块后(After register)，scanJars: "+scanJars);
    }

    private static void extractWebFromModule(String jar) {
        log.info("从模块(Extract web resource from)："+jar+" 中提取web资源");
        String loc=FileUtils.getAbsolutePath("/");
        jar=FileUtils.getAbsolutePath(jar);
        ZipUtils.unZip(jar, "web", loc, true);
        log.info("jar："+jar);
        log.info("loc："+loc);
    }

    private static void extractDataFromModule(String jar) {
        log.info("从模块(Extract data from)："+jar+" 中提取数据");
        String loc=FileUtils.getAbsolutePath("/WEB-INF/classes/data/");
        jar=FileUtils.getAbsolutePath(jar);
        ZipUtils.unZip(jar, "data/init", loc, true);
        log.info("jar："+jar);
        log.info("loc："+loc);
    }
}