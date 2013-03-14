package com.apdplat.module.security.service;

import com.apdplat.platform.log.APDPlatLogger;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 *此工具负责把
 * LinuxSequenceService.class
 * WindowsSequenceService.class
 * MacSequenceService.class
 * SolarisSequenceService.class
 * SecurityService.class
 * 进行加密并放到WEB模块
 * @author ysc
 */
public class EncryptClassUtils {
    protected static final APDPlatLogger log = new APDPlatLogger(EncryptClassUtils.class);
    private static String sequenceKeyName;
    private static String securityKeyName;
    private static String winClspath;
    private static String linuxClspath;
    private static String macClspath;
    private static String solarisClspath;
    private static String securityClspath;
    
    private static final String workDir="D:/Workspaces/NetBeansProjects/APDPlat2/APDPlat_Core/";
    static{        
        String dir=workDir+"src/main/resources/com/apdplat/module/security/service/";
        sequenceKeyName = dir+"SequenceKey";
        securityKeyName = dir+"SecurityKey";
        winClspath = dir+"WindowsSequenceService";
        linuxClspath = dir+"LinuxSequenceService";
        macClspath = dir+"MacSequenceService";
        solarisClspath = dir+"SolarisSequenceService";
        securityClspath = dir+"SecurityService";
    }

    private static void createKey(String keyName) {
        File file = new File(keyName);
        if (file.exists()) {
            return;
        }
        try {
            // 创建一个可信任的随机数源，DES算法需要
            SecureRandom sr = new SecureRandom();
            // 用DES算法创建一个KeyGenerator对象
            KeyGenerator kg = KeyGenerator.getInstance("DES");
            // 初始化此密钥生成器,使其具有确定的密钥长度
            kg.init(sr);
            // 生成密匙
            SecretKey key = kg.generateKey();
            // 获取密钥数据
            byte rawKeyData[] = key.getEncoded();
            try (FileOutputStream fo = new FileOutputStream(file)) {
                fo.write(rawKeyData);
            }
        } catch (NoSuchAlgorithmException | IOException e) {
            log.error("创建私钥失败",e);
        }
    }

    private static void encrypt(String keyFile, String classFile, String newClassFile) {
        try {
            // DES算法要求有一个可信任的随机数源
            SecureRandom sr = new SecureRandom();
            byte[] rawKeyData;
            try (FileInputStream fi = new FileInputStream(new File(keyFile))) {
                rawKeyData = readAll(fi);
            }
            // 从原始密匙数据创建DESKeySpec对象
            DESKeySpec dks = new DESKeySpec(rawKeyData);
            // 创建一个密匙工厂，然后用它把DESKeySpec转换成一个SecretKey对象
            SecretKey key = SecretKeyFactory.getInstance("DES").generateSecret(dks);
            // Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance("DES");
            // 用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, key, sr);
            byte[] data;
            try (FileInputStream fi2 = new FileInputStream(new File(classFile))) {
                data = readAll(fi2);
            }
            // 正式执行加密操作
            byte encryptedData[] = cipher.doFinal(data);
            // 用加密后的数据覆盖原文件
            File file = new File(newClassFile);
            file.createNewFile();
            try (FileOutputStream fo = new FileOutputStream(file)) {
                fo.write(encryptedData);
            }
        } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | IOException e) {
            log.error("加密失败",e);
        }
    }

    public static void main(String args[]) throws Exception {
        createKey(sequenceKeyName);
        createKey(securityKeyName);
        encrypt(sequenceKeyName, workDir+"target/classes/com/apdplat/module/security/service/WindowsSequenceService.class", winClspath);
        encrypt(sequenceKeyName, workDir+"target/classes/com/apdplat/module/security/service/LinuxSequenceService.class", linuxClspath);
        encrypt(sequenceKeyName, workDir+"target/classes/com/apdplat/module/security/service/MacSequenceService.class", macClspath);
        encrypt(sequenceKeyName, workDir+"target/classes/com/apdplat/module/security/service/SolarisSequenceService.class", solarisClspath);
        encrypt(securityKeyName, workDir+"target/classes/com/apdplat/module/security/service/SecurityService.class", securityClspath);
    }
    private static byte[] readAll(InputStream in){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            for (int n ; (n = in.read(buffer))>0 ; ) {
                out.write(buffer, 0, n);
            }
        } catch (IOException e) {
            log.error("读取数据失败",e);
        }
        return out.toByteArray();
    }
}
