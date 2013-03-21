package com.apdplat.platform.util;

import com.apdplat.platform.log.APDPlatLogger;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.LinkedHashSet;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

/**
 *
 * @author ysc
 */
public class FileUtils {
    protected static final APDPlatLogger log = new APDPlatLogger(FileUtils.class);
        
    private FileUtils(){};
    
    private static String basePath=System.getProperty("user.dir")+File.separator;

    /**
     * 追加写入文件
     * @param path 文件绝对路径
     * @param text 要追加的文件内容
     * @return 
     */
    public static boolean appendText(String path,String text){
        try{
            File file=new File(path);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file,true))) {
                writer.write(text);
            }
            return true;
        }catch(Exception e){
            log.error("写文件出错",e);
        }
        return false;
    }
    /**
     * 系统的默认目录为当前用户目录，可通过此函数重新设置
     * @param basePath 
     */
    public static void setBasePath(String basePath) {
        Assert.notNull(basePath);
        
        if(!basePath.trim().endsWith(File.separator)){
            basePath+=File.separator;
        }
        FileUtils.basePath = basePath;
    }
   
    /**
     * 获取web根目录下面的资源的绝对路径
     * @param path 相对应WEB根目录的路径
     * @return 绝对路径
     */
    public static String getAbsolutePath(String path){
        Assert.notNull(path);
        //在windows下，如果路径包含：,为绝对路径，则不进行转换
        if(path.contains(":")){
            return path;
        }
        
        log.debug("转换路径:"+path);
        if(path!=null && path.trim().length()==1){
            return basePath;
        }
        if(path.startsWith("/")){
            path=path.substring(1);
        }
        path=basePath+path.replace("/", File.separator);
        log.debug("返回路径:"+path);
        return path;
    }
    public static void copyFile(File inFile, File outFile){
        try {
            copyFile(new FileInputStream(inFile),outFile);
        } catch (FileNotFoundException ex) {
            log.error("文件不存在",ex);
        }
    }
    public static void copyFile(InputStream in, File outFile){
        OutputStream out = null;
        try {
            byte[] data=readAll(in);
            out = new FileOutputStream(outFile);
            out.write(data, 0, data.length);
            out.close();
        } catch (Exception ex) {
            log.error("文件操作失败",ex);
        } finally {
            try {
                if(in!=null){
                    in.close();
                }
            } catch (IOException ex) {
             log.error("文件操作失败",ex);
            }
            try {
                if(out!=null){
                    out.close();
                }
            } catch (IOException ex) {
             log.error("文件操作失败",ex);
            }
        }
    }
    
    public static byte[] readAll(File file){
        try {
            return readAll(new FileInputStream(file));
        } catch (Exception ex) {
            log.error("读取文件失败",ex);
        }
        return null;
    }
    
    public static byte[] readAll(InputStream in){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            for (int n ; (n = in.read(buffer))>0 ; ) {
                out.write(buffer, 0, n);
            }
        } catch (IOException ex) {
            log.error("读取文件失败",ex);
        }
        return out.toByteArray();
    }

    public static FileInputStream getInputStream(String path) {
        try {
            return new FileInputStream(getAbsolutePath(path));
        } catch (FileNotFoundException ex) {
            log.error("文件没有找到",ex);
        }
        return null;
    }
    public static boolean existsFile(String path){
        try{
            File file=new File(getAbsolutePath(path));
            if(file.exists()){
                return true;
            }
        }catch(Exception ex){
            log.error("文件操作失败",ex);
        }
        return false;
    }
    public static File createAndWriteFile(String path,byte[] data){
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
            log.error("文件操作失败",ex);
        }
        return null;
    }
    public static File createAndWriteFile(String path,String text){
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
            log.error("文件操作失败",ex);
        }
        return null;
    }
    
    public static boolean removeFile(String path){
        try{
            File file=new File(getAbsolutePath(path));
            if(file.exists()){
                file.delete();
            }
            return true;
        }catch(Exception ex){
            log.error("文件操作失败",ex);
        }
        return false;
    }

    public static Collection<String> getTextFileContent(String path) {
        return getTextFileContent(getInputStream(path));
    }

    public static Collection<String> getTextFileContent(InputStream in) {
        Collection<String> result=new LinkedHashSet<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
            String line=reader.readLine();
            while(line!=null){
                //忽略空行和以#号开始的注释行
                if(!"".equals(line.trim()) && !line.trim().startsWith("#")){
                    result.add(line);
                }
                line=reader.readLine();
            }
        } catch (UnsupportedEncodingException ex) {
            log.error("不支持的编码",ex);
        }  catch (IOException ex) {
            log.error("文件操作失败",ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                log.error("文件操作失败",ex);
            }
        }
        return result;
    }
    public static Collection<String> getClassPathTextFileContent(String path) {
        try {
            ClassPathResource cr = new ClassPathResource(path);
            return getTextFileContent(cr.getInputStream());
        } catch (IOException ex) {
            log.error("文件操作失败",ex);
        }
        return null;
    }
}
