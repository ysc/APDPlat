package com.apdplat.module.system.service.backup;

import com.apdplat.module.monitor.model.BackupLog;
import com.apdplat.module.monitor.model.BackupLogResult;
import com.apdplat.module.monitor.model.BackupLogType;
import com.apdplat.module.security.model.User;
import com.apdplat.module.security.service.UserHolder;
import com.apdplat.module.system.service.LogQueue;
import com.apdplat.module.system.service.PropertyHolder;
import com.apdplat.module.system.service.SystemListener;
import com.apdplat.platform.log.APDPlatLogger;
import com.apdplat.platform.util.FileUtils;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;

/**
 *备份恢复服务定义
 * @author ysc
 */
public abstract class BackupService {  
    protected final APDPlatLogger log = new APDPlatLogger(getClass());
    
    protected static final StandardPBEStringEncryptor encryptor;
    protected static final String username;
    protected static final String password;
    //从配置文件中获取数据库用户名和密码，如果用户名和密码被加密，则解密
    static{
            EnvironmentStringPBEConfig config=new EnvironmentStringPBEConfig();
            config.setAlgorithm("PBEWithMD5AndDES");
            config.setPassword("config");

            encryptor=new StandardPBEStringEncryptor();
            encryptor.setConfig(config);
            String uname=PropertyHolder.getProperty("db.username");
            String pwd=PropertyHolder.getProperty("db.password");
            if(uname!=null && uname.contains("ENC(") && uname.contains(")")){
                uname=uname.substring(4,uname.length()-1);
                username=decrypt(uname);
            }else{
                username=uname;
            }
            if(pwd!=null && pwd.contains("ENC(") && pwd.contains(")")){
                pwd=pwd.substring(4,pwd.length()-1);
                password=decrypt(pwd);
            }else{
                password=pwd;
            }
    }
    /**
     * 把备份和恢复的通用逻辑抽取出来，并执行日志记录工作
     * @param type 备份或是恢复
     * @param backup  true为备份，false为恢复
     * @param date 数据库恢复到哪一个时间点
     * @return 
     */
    public boolean common(String type,boolean backup,String date){
        User user=UserHolder.getCurrentLoginUser();
        String ip=UserHolder.getCurrentUserLoginIp();
        boolean enableBackup=PropertyHolder.getBooleanProperty("monitor.backup");
        BackupLog backupLog=null;
        
        if(enableBackup){
            backupLog=new BackupLog();
            backupLog.setOwnerUser(user);
            backupLog.setLoginIP(ip);
            try {
                backupLog.setServerIP(InetAddress.getLocalHost().getHostAddress());
            } catch (UnknownHostException e) {
                log.error("记录备份日志出错",e);
            }
            backupLog.setAppName(SystemListener.getContextPath());
            backupLog.setStartTime(new Date());
            backupLog.setOperatingType(type);
        }
        boolean result=false;
        try{
            if(backup){
                result=backupImpl();   
            }else{
                result=restoreImpl(date);   
            }                     
        }catch(Exception e){            
            log.error("备份出错",e);
        }
        
        if(enableBackup){
            if(result){
                backupLog.setOperatingResult(BackupLogResult.SUCCESS);
            }else{
                backupLog.setOperatingResult(BackupLogResult.FAIL);
            }
            backupLog.setEndTime(new Date());
            backupLog.setProcessTime(backupLog.getEndTime().getTime()-backupLog.getStartTime().getTime());
            LogQueue.addLog(backupLog);
        }
        return result;
    }
    /**
     * 备份数据库服务定义
     * @return 
     */
    public boolean backup(){
        return common(BackupLogType.BACKUP,true,null);
    }
    /**
     * 备份数据库具体实现，留给子类实现
     * @return 
     */
    public abstract boolean backupImpl();
    /**
     * 恢复数据库服务定义
     * @param date
     * @return 
     */
    public boolean restore(String date){
        return common(BackupLogType.RESTORE,false,date);
    }
    /**
     * 恢复数据库具体实现，留给子类实现
     * @param date
     * @return 
     */
    public abstract boolean restoreImpl(String date);
    /**
     * 获取备份文件保存的文件系统路径
     * @return 备份文件存放路径
     */
    public static String getPath(){
        String path="/WEB-INF/backup/"+PropertyHolder.getProperty("jpa.database")+"/";
        path=FileUtils.getAbsolutePath(path);
        File file=new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
        return path;
    }
    /**
     * 获取已经存在的备份文件列表
     * @return  备份文件列表
     */
    public List<String> getExistBackup(){
        List<String> result=new ArrayList<>();
        String path=getPath();
        File dir=new File(path);
        File[] files=dir.listFiles();
        for(File file : files){
            String name=file.getName();
            if(!name.contains("bak")) {
                continue;
            }
            name=name.substring(0, name.length()-4);
            String[] temp=name.split("-");
            String y=temp[0];
            String m=temp[1];
            String d=temp[2];
            String h=temp[3];
            String mm=temp[4];
            String s=temp[5];
            name=y+"-"+m+"-"+d+" "+h+":"+mm+":"+s;
            result.add(name);
        }

        return result;
    }
    /**
     * 解密用户名和密码
     * @param encryptedMessage 加密后的用户名或密码
     * @return 解密后的用户名或密码
     */
    protected static String decrypt(String encryptedMessage){
        String plain=encryptor.decrypt(encryptedMessage);
        return plain;
    }  
}
