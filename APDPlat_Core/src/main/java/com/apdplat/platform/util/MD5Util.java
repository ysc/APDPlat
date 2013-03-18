package com.apdplat.platform.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * md5算法工具
 * @author ysc
 */
public class MD5Util {
    private static final Log log = LogFactory.getLog(MD5Util.class);
    static MessageDigest md = null;

    static {
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ne) {
            log.error("NoSuchAlgorithmException: md5", ne);
        }
    }

    /**
     * 对一个文件求他的md5值
     * @param f 要求md5值的文件
     * @return md5串
     */
    public static String md5(File f) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fis.read(buffer)) != -1) {
                md.update(buffer, 0, length);
            }

            return new String(Hex.encodeHex(md.digest()));
        } catch (FileNotFoundException e) {
            log.error("md5 file " + f.getAbsolutePath() + " failed:" + e.getMessage());
            return null;
        } catch (IOException e) {
            log.error("md5 file " + f.getAbsolutePath() + " failed:" + e.getMessage());
            return null;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException ex) {
             log.error("文件操作失败",ex);
            }
        }
    }

    /**
     * 求一个字符串的md5值
     * @param target 字符串
     * @return md5 value
     */
    public static String md5(String target) {
        return DigestUtils.md5Hex(target);
    }
    /**
     * 可以比较两个文件是否内容相等
     * @param args 
     */
    public static void main(String[] args){
        File newFile=new File("D:/files/paoding-analysis.jar.new");
        File oldFile=new File("D:/files/paoding-analysis.jar.old");
        String s1=md5(newFile);
        String s2=md5(oldFile);
        System.out.println(s1.equals(s2));
        System.out.println(s1);
        System.out.println(s2);
    }
}