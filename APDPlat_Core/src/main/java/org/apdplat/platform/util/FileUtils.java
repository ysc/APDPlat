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

import org.apdplat.platform.log.APDPlatLogger;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

/**
 * 文件操作类
 * 可在生产环境（Web容器）下和开发环境下保持一致的使用方法
 * 对于相对路径，可通过setBasePath方法指定前缀
 * 开发环境下的相对路径其前缀为用户当前目录：System.getProperty("user.dir")
 * 生产环境（Web容器）下的相对路径其前缀为应用的根目录：servletContextEvent.getServletContext().getRealPath("/")
 * @author 杨尚川
 */
public class FileUtils {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(FileUtils.class);
        
    /**
     * 工具类，无实例对象，仅仅提供静态方法
     */
    private FileUtils(){};
    
    private static String basePath=System.getProperty("user.dir").replace("\\", "/");
    
    /**
     * 系统的默认目录为当前用户目录，可通过此函数重新设置
     * @param basePath 相对路径前缀
     */
    public static void setBasePath(String basePath) {
        Assert.notNull(basePath);        
        FileUtils.basePath = basePath.replace("\\", "/");
    }
    /**
     * 追加写入文件 -- 另起一行
     * @param path 文件绝对路径
     * @param text 要追加的文件内容
     * @return 是否追加成功
     */
    public static boolean appendTextInNewLine(String path,String text){
        return appendText(path,text+System.getProperty("line.separator"));
    }
    /**
     * 追加写入文件
     * @param path 文件绝对路径
     * @param text 要追加的文件内容
     * @return 是否追加成功
     */
    public static boolean appendText(String path,String text){
        try{
            File file=new File(path);
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true), "utf-8"))) {
                writer.write(text);
                writer.close();
            }
            return true;
        }catch(IOException e){
            LOG.error("写文件出错",e);
        }
        return false;
    }
   
    /**
     * 获取 web根目录或当前工作目录或指定目录 下面的资源的绝对路径
     * / 返回 web根目录或当前工作目录或指定目录 的绝对路径
     * /WEB-INF 返回 web根目录或当前工作目录或指定目录 下的WEB-INF 的绝对路径
     * WEB-INF 返回 web根目录或当前工作目录或指定目录 下的WEB-INF 的绝对路径
     * 
     * 特殊：对windows下的绝对路径不做转换    
     * c:/test/test.txt 返回 c:/test/test.txt
     * 
     * @param path 相对于WEB根目录的路径
     * @return 绝对路径
     */
    public static String getAbsolutePath(String path){
        Assert.notNull(path);
        //在windows下，如果路径包含：,为绝对路径，则不进行转换
        if(path.contains(":")){
            return path.replace("\\", "/");
        }        
        LOG.debug("转换路径:"+path);        
        path=path.replace("\\", "/").trim();        
        path=basePath+"/"+path;
        while(path.contains("//")){
            path=path.replace("//", "/");
        }
        LOG.debug("返回路径:"+path);
        return path;
    }
    /**
     * 文件复制
     * @param inFile 输入文件
     * @param outFile 输出文件
     */
    public static void copyFile(File inFile, File outFile){
        try {
            copyFile(new FileInputStream(inFile),outFile);
        } catch (FileNotFoundException ex) {
            LOG.error("文件不存在",ex);
        }
    }
    /**
     * 把输入流中的内容拷贝到一个文件
     * @param in 输入流
     * @param outFile 文件对象
     */
    public static void copyFile(InputStream in, File outFile){
        OutputStream out = null;
        try {
            byte[] data=readAll(in);
            out = new FileOutputStream(outFile);
            out.write(data, 0, data.length);
            out.close();
        } catch (Exception ex) {
            LOG.error("文件操作失败",ex);
        } finally {
            try {
                if(in!=null){
                    in.close();
                }
            } catch (IOException ex) {
             LOG.error("文件操作失败",ex);
            }
            try {
                if(out!=null){
                    out.close();
                }
            } catch (IOException ex) {
             LOG.error("文件操作失败",ex);
            }
        }
    }
    /**
     * 读取一个文件的所有字节
     * @param file 文件对象
     * @return 字节数组
     */
    public static byte[] readAll(File file){
        try {
            return readAll(new FileInputStream(file));
        } catch (Exception ex) {
            LOG.error("读取文件失败",ex);
        }
        return null;
    }
    /**
     * 从输入流中读取所有字节
     * @param in 输入流
     * @return 字节数组
     */
    public static byte[] readAll(InputStream in){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            for (int n ; (n = in.read(buffer))>0 ; ) {
                out.write(buffer, 0, n);
            }
        } catch (IOException ex) {
            LOG.error("读取文件失败",ex);
        }
        return out.toByteArray();
    }
    /**
     * 获取一个文件的输入流
     * @param path 相对路径或绝对路径
     * @return 文件输入流
     */
    public static FileInputStream getInputStream(String path) {
        try {
            return new FileInputStream(getAbsolutePath(path));
        } catch (FileNotFoundException ex) {
            LOG.error("文件没有找到",ex);
        }
        return null;
    }
    /**
     * 判断一个文件是否存在
     * @param path 相对路径或绝对路径
     * @return 是否存在
     */
    public static boolean existsFile(String path){
        try{
            File file=new File(getAbsolutePath(path));
            if(file.exists()){
                return true;
            }
        }catch(Exception ex){
            LOG.error("文件操作失败",ex);
        }
        return false;
    }
    /**
     * 把字节内容写入新文件
     * @param path 相对路径或绝对路径
     * @param data 字节数组
     * @return 新文件
     */
    public static File createAndWriteFile(String path, byte[] data){
        try{
            File file=new File(getAbsolutePath(path));
            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
            try (OutputStream out = new FileOutputStream(file)) {
                out.write(data, 0, data.length);
            }
            return file;
        }catch(Exception ex){
            LOG.error("文件操作失败",ex);
        }
        return null;
    }
    /**
     * 把文本内容写入一个新文件
     * 如果文件已经存在
     * 则覆盖
     * @param path 相对路径或绝对路径
     * @param text 文本
     * @return 文件
     */
    public static File createAndWriteFile(String path, String text){
        try{
            File file=new File(getAbsolutePath(path));
            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"utf-8"))) {
                writer.write(text);
            }
            return file;
        }catch(Exception ex){
            LOG.error("文件操作失败",ex);
        }
        return null;
    }
    /**
     * 删除文件
     * @param path 相对路径或绝对路径
     * @return 是否成功
     */
    public static boolean removeFile(String path){
        try{
            File file=new File(getAbsolutePath(path));
            if(file.exists()){
                if(!file.delete()){
                    file.deleteOnExit();
                }
            }
            return true;
        }catch(Exception ex){
            LOG.error("文件操作失败",ex);
        }
        return false;
    }
    /**
     * 获取文本内容
     * @param path 相对路径或绝对路径
     * @return 行的列表
     */
    public static List<String> getTextFileContent(String path) {
        try {
            return getTextFileContent(new FileInputStream(getAbsolutePath(path)));
        } catch (FileNotFoundException ex) {
            if(!path.contains("apdplat.licence")){
                LOG.error("文件不存在", ex);
            }
        }
        //Null Object设计模式
        return Collections.emptyList();
    }
    /**
     * 获取输入流中的文本内容
     * @param in 文本文件输入流
     * @return 行的列表
     */
    public static List<String> getTextFileContent(InputStream in) {
        List<String> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"))) {
            for (;;) {
                String line = reader.readLine();
                if (line == null)
                    break;
                result.add(line);
            }
        } catch(Exception e){
            LOG.error("读取文件失败",e);
        }
        return result;
    }
    /**
     * 获取类路径下面的文本文件的内容
     * @param path 类路径下面的文本文件
     * @return 行的集合
     */
    public static Collection<String> getClassPathTextFileContent(String path) {
        try {
            ClassPathResource cr = new ClassPathResource(path);
            return getTextFileContent(cr.getInputStream());
        } catch (IOException ex) {
            LOG.error("获取类路径资源失败",ex);
        }
        //Null Object设计模式
        return Collections.emptyList();
    }
}