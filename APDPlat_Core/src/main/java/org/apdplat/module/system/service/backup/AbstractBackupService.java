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

package org.apdplat.module.system.service.backup;

import org.apdplat.module.system.service.PropertyHolder;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.util.FileUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;

/**
 *备份恢复数据库抽象类，抽象出了针对各个数据库来说通用的功能
 * @author 杨尚川
 */
public abstract class AbstractBackupService implements BackupService{  
    protected final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(getClass());
    
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
    @Override
    public String getBackupFilePath(){
        String path="/WEB-INF/backup/"+PropertyHolder.getProperty("jpa.database")+"/";
        path=FileUtils.getAbsolutePath(path);
        File file=new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
        return path;
    }
    @Override
    public File getNewestBackupFile(){
        Map<String,File> map = new HashMap<>();
        List<String> list = new ArrayList<>();
        String path=getBackupFilePath();
        File dir=new File(path);
        File[] files=dir.listFiles();
        for(File file : files){
            String name=file.getName();
            if(!name.contains("bak")) {
                continue;
            }
            map.put(name, file);
            list.add(name);
        }
        if(list.isEmpty()){
            return null;
        }
        //按备份时间排序
        Collections.sort(list);
        //最新备份的在最前面
        Collections.reverse(list);
        
        String name = list.get(0);
        File file = map.get(name);
        //加速垃圾回收
        list.clear();
        map.clear();
        
        return file;
    }
    @Override
    public List<String> getExistBackupFileNames(){
        List<String> result=new ArrayList<>();
        String path=getBackupFilePath();
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
        //按备份时间排序
        Collections.sort(result);
        //最新备份的在最前面
        Collections.reverse(result);

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