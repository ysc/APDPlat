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

package org.apdplat.module.system.service.backup.impl;

import java.io.File;
import javax.annotation.Resource;
import org.apdplat.module.system.service.PropertyHolder;
import org.apdplat.module.system.service.backup.BackupFileSender;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.apdplat.platform.util.FtpUtils;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.stereotype.Service;

/**
 * 将备份文件发送到FTP服务器上面
 * @author 杨尚川
 */
@Service
public class FtpBackupFileSender implements BackupFileSender{
    protected final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(getClass());
    
    @Resource(name="ftpUtils")
    private FtpUtils ftpUtils;
    
    @Resource(name="configurationEncryptor")
    private StandardPBEStringEncryptor configurationEncryptor;
    
    @Override
    public void send(File file) {
        try{
            String host = PropertyHolder.getProperty("ftp.server.host");
            int port = PropertyHolder.getIntProperty("ftp.server.port");
            String username = PropertyHolder.getProperty("ftp.server.username");
            String password = PropertyHolder.getProperty("ftp.server.password");
            if(username!=null && username.contains("ENC(") && username.contains(")")){
                username=username.substring(4,username.length()-1);
            }
            if(password!=null && password.contains("ENC(") && password.contains(")")){
                password=password.substring(4,password.length()-1);
            }        
            username = configurationEncryptor.decrypt(username);
            password = configurationEncryptor.decrypt(password);
            String dist = PropertyHolder.getProperty("log.backup.file.ftp.dir");
            String database = PropertyHolder.getProperty("jpa.database");
            dist = dist.replace("${database}", database);
            LOG.info("本地备份文件："+file.getAbsolutePath());
            LOG.info("FTP服务器目标目录："+dist);
            boolean connect = ftpUtils.connect(host, port, username, password);
            if(connect){
                boolean result = ftpUtils.uploadTo(file, dist);
                if(result){
                    LOG.info("备份文件上传到FTP服务器成功");
                }else{
                    LOG.error("备份文件上传到FTP服务器失败");
                }
            }
        }catch(Exception e){
            LOG.error("备份文件上传到FTP服务器失败",e);
        }
    }
}
