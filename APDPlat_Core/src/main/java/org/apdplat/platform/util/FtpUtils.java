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

package org.apdplat.platform.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.springframework.stereotype.Service;

/**
 * FTP操作工具
 * @author 杨尚川
 */
@Service
public class FtpUtils {
    protected final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(getClass());

    private FTPClient ftpClient;

    /**
     * 连接FTP服务器
     * @param host FTP服务器地址
     * @param port FTP服务器端口号
     * @param username 用户名
     * @param password 密码
     * @return
     */
    public boolean connect(String host, int port, String username, String password) {
        try{
            ftpClient = new FTPClient();
            ftpClient.connect(host, port);
            ftpClient.login(username, password);
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                LOG.error("连接FTP服务器失败，响应码："+reply);
                return false;
            }
        }catch(IOException e){
            LOG.error("连接FTP服务器失败",e);
            return false;
        }
        return true;
    }
    /**
     * 上传文件到服务器上的特定路径
     * @param file 上传的文件或文件夹
     * @param path 上传到ftp服务器哪个路径下
     * @return 
     */
    public boolean uploadTo(File file, String path){
        //切换到服务器上面的合适目录
        //如果对应的目录不存在，则创建
        LOG.info("上传文件 "+file.getAbsolutePath()+" 到服务器路径 "+path);
        try{
            String[] segs = path.split("/");
            for(String seg : segs){
                if(!ftpClient.changeWorkingDirectory(seg)){
                    ftpClient.makeDirectory(seg);
                    if(!ftpClient.changeWorkingDirectory(seg)){
                        LOG.error("服务器目录切换错误:"+seg);
                        return false;
                    }
                }
            }
        }catch(IOException e){
            LOG.error("服务器目录切换错误",e);
            return false;
        }
        return upload(file);
    }
    /**
     * 上传文件
     * @param file 上传的文件或文件夹
     * @return 是否上次成功
     */
    private boolean upload(File file) {
        try{
            if (file.isDirectory()) {
                ftpClient.makeDirectory(file.getName());
                ftpClient.changeWorkingDirectory(file.getName());
                File[] subFiles = file.listFiles();
                for (File subFile : subFiles) {
                    if (subFile.isDirectory()) {
                        upload(subFile);
                        ftpClient.changeToParentDirectory();
                    } else {
                        try (FileInputStream input = new FileInputStream(subFile)) {
                            ftpClient.storeFile(subFile.getName(), input);
                        }
                    }
                }
            } else {
                try (FileInputStream input = new FileInputStream(file)) {
                    ftpClient.storeFile(file.getName(), input);
                }
            }
        }catch(IOException e){
            LOG.error("上传文件失败",e);
            return false;
        }
        return true;
    }

    public static void main(String[] args) throws Exception {
        FtpUtils ftp = new FtpUtils();
        //File file = new File("C:\\apache-ant-1.8.4");
        //File file = new File("C:\\Users\\ysc\\Downloads\\2014-01-30-07-22-50.zip");
        File file = new File("C:\\Users\\ysc\\Downloads\\OperateLogAspect.java");
        if(ftp.connect("localhost", 21, "admin", "test")){
            if(ftp.uploadTo(file,"java/apdplat/core")){
                System.out.println("上传成功");
            }else{
                System.out.println("上传失败");
            }
        }
    }
}