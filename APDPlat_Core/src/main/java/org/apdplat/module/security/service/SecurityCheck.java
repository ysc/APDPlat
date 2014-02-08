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

package org.apdplat.module.security.service;

/**
 *
 * @author 杨尚川
 */
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.util.FileUtils;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

@Service
public class SecurityCheck {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(SecurityCheck.class);
    private static final String securityKeyName;
    private static final String securityClspath;
    private static final String sequenceKeyName;
    private static final String sequenceClspath;
    private static String osName="Windows";
    static{       
        if(System.getProperty("os.name").toLowerCase().indexOf("linux")!=-1){
            osName="Linux";
        }
        if(System.getProperty("os.name").toLowerCase().indexOf("mac")!=-1){
            osName="Mac";
        }
        if(System.getProperty("os.name").toLowerCase().indexOf("solaris")!=-1){
            osName="Solaris";
        }
        LOG.debug("osName: "+osName); 
        sequenceKeyName="/org/apdplat/module/security/service/sequence/SequenceKey";
        sequenceClspath="/org/apdplat/module/security/service/sequence/"+osName+"SequenceService";
    
        securityKeyName="/org/apdplat/module/security/service/SecurityKey";
        securityClspath="/org/apdplat/module/security/service/SecurityService";
    }
    /**
     * 
     * @param <T>
     * @param keyFile 类路径
     * @param classFile 类路径
     * @param className
     * @return 
     */
    private static <T> T loadClass(String keyFile,String classFile,final String className) {
        try {
            // DES算法要求有一个可信任的随机数源
            SecureRandom sr = new SecureRandom();
            byte[] rawKeyData;
            try (InputStream fi = SecurityCheck.class.getResourceAsStream(keyFile)) {
                rawKeyData = FileUtils.readAll(fi);
            }
            // 从原始密匙数据创建一个DESKeySpec对象
            DESKeySpec dks = new DESKeySpec(rawKeyData);
            // 创建一个密匙工厂，然后用它把DESKeySpec对象转换成一个SecretKey对象
            SecretKey key = SecretKeyFactory.getInstance("DES").generateSecret(dks);
            // Cipher对象实际完成解密操作
            Cipher cipher = Cipher.getInstance("DES");
            // 用密匙初始化Cipher对象
            cipher.init(Cipher.DECRYPT_MODE, key, sr);
            byte[] encryptedData;
            try (InputStream fi2 = SecurityCheck.class.getResourceAsStream(classFile)) {
                encryptedData = FileUtils.readAll(fi2);
            }
            // 正式执行解密操作
            final byte decryptedData[] = cipher.doFinal(encryptedData);            
            Object obj = new ClassLoader(){
                @Override
                public Class<?> loadClass(String name) throws ClassNotFoundException {
                    if(className.equals(name)){
                        return super.defineClass(name, decryptedData, 0, decryptedData.length);    
                    }
                    
                    return SecurityCheck.class.getClassLoader().loadClass(name);
                }
            }.loadClass(className);
            return (T)obj;
        } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | ClassNotFoundException e) {
            LOG.debug("加载类失败",e);
        } 
        return null;
    }
    private static String getSequence(){
        try {
            Class clazz=loadClass(sequenceKeyName,sequenceClspath,"org.apdplat.module.security.service.sequence."+osName+"SequenceService");
            Object obj=clazz.newInstance();
            Method method=ReflectionUtils.findMethod(clazz, "getSequence");
            String seq=method.invoke(obj).toString();
            return seq;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            LOG.debug("获取机器码钥失败",e);
        }
        return "";
    }
    @PostConstruct
    public static  void check(){
        LOG.debug("开始进行安全检查");        
        String seq="";
        try{
            seq=getSequence();
            LOG.debug("机器指纹："+seq);
        }catch(Exception e){
            LOG.debug("安全检查失败",e);
        }
        try {
            Class clazz=loadClass(securityKeyName,securityClspath,"org.apdplat.module.security.service.SecurityService");
            Object obj=clazz.newInstance();
            Method method=ReflectionUtils.findMethod(clazz, "checkSeq",String.class);
            method.invoke(obj,seq);
            LOG.debug("安全检查完成");
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            LOG.debug("安全检查出错",e);
        }
    }
}