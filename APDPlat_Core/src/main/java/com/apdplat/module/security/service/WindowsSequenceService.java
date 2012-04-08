package com.apdplat.module.security.service;

import com.apdplat.platform.util.ConvertUtils;
import com.apdplat.platform.util.FileUtils;
import java.io.BufferedReader; 
import java.io.InputStream;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.LoggerFactory;

/**
 *在Windows平台上生成机器码
 * @author ysc
 */
public final class WindowsSequenceService implements SequenceService{

    private static final Logger log = LoggerFactory.getLogger(WindowsSequenceService.class);
    private static String seqExe="";
    
    private static File extractExe() {
        String exeFile="/WEB-INF/classes/data/sequence/SequenceGenerator.exe";
        String exeClspath="/com/apdplat/module/security/service/SequenceGenerator";        
        InputStream fi = SecurityCheck.class.getResourceAsStream(exeClspath);
        byte exeData[] = FileUtils.readAll(fi);
        File file=FileUtils.createAndWriteFile(exeFile, exeData);   
        return file;
    }
    
    private static boolean verifyExe() {
        File file=extractExe();
        seqExe=file.getAbsolutePath();
        
        String md5=getMD5(file);
        log.debug("exe md5:"+md5);
        if("154d4c4ad44ff4755c884cccb875142d73216".toUpperCase().equals(md5.toUpperCase())){
            return true;
        }
        return false;
    }

    @Override
    public String getSequence() { 
        /*
        StringBuilder result = new StringBuilder();
        //验证exe的数字摘要，以保证其未被其他人修改过
        if (verifyExe()) {
            log.debug("exe验证通过");
            try {
                Runtime runtime = Runtime.getRuntime();
                Process child = runtime.exec(seqExe);
                InputStream in = child.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf8"));
                String line = reader.readLine();
                if (line != null) {
                    result.append(line);
                } else {
                    log.debug("没有获取到机器码");
                    return System.currentTimeMillis()+"";
                }
                line = reader.readLine();
                if (line != null) {
                    log.debug("获取机器码出错，返回了两行数据");
                    return System.currentTimeMillis()+"";
                }

                reader.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                return System.currentTimeMillis()+"";
            }
        } else {
            log.debug("exe验证失败");
            return System.currentTimeMillis()+"";
        }
        String info=result.toString();
        log.debug("seq exe path: " + seqExe);
        log.debug("机器信息："+info);
        StringBuilder code=new StringBuilder();
        String[] attr=info.split("_");
        for(String item : attr){
            String[] temp=item.split(":");
            if(temp.length==2){
                if(StringUtils.isNotBlank(temp[1]) && temp[1].length()>3){
                    code.append(temp[0]).append(":").append(temp[1]).append("_");
                }
            }
        }
        if(code.length()>0){
            code.deleteCharAt(code.length()-1);
        }
        info=code.toString();
        log.debug("修正后的机器信息："+info);
        String machineCode = getMD5(info);
        log.debug("机器码摘要："+machineCode);
        return machineCode;
         * 
         */
        String machineCode = getMD5(getCPUSerial()+getHDSerial("C"));
        return machineCode;
    }
    
    /**
     * 对文件全文生成MD5摘要
     * @param file   要加密的文件
     * @return MD5摘要码
     */
    private static String getMD5(File file) {
        FileInputStream fis = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            log.debug("MD5摘要长度：" + md.getDigestLength());
            fis = new FileInputStream(file);
            byte[] buffer = new byte[2048];
            int length = -1;
            log.debug("开始生成摘要");
            long s = System.currentTimeMillis();
            while ((length = fis.read(buffer)) != -1) {
                md.update(buffer, 0, length);
            }
            log.debug("摘要生成成功,总用时: " + (System.currentTimeMillis() - s)
                    + "ms");
            byte[] b = md.digest();
            return ConvertUtils.byte2HexString(b)+file.length();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 对一段String生成MD5加密信息
     * @param message 要加密的String
     * @return 生成的MD5信息
     */
    private static String getMD5(String message) {
        message += "{apdplat}";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            log.debug("MD5摘要长度：" + md.getDigestLength());
            byte[] b = md.digest(message.getBytes("utf-8"));
            return ConvertUtils.byte2HexString(b)+message.length();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void main(String[] args) {        
        seqExe = "D:/Workspaces/NetBeans7.0/APDPlat2.2/APDPlat_Web/src/main/resources/data/sequence/SequenceGenerator.exe";
        WindowsSequenceService s = new WindowsSequenceService();
        String seq = s.getSequence();
        System.out.println(seq);
    }
    
   
    /**
     *
     * @param drive 硬盘驱动器分区 如C,D
     * @return 该分区的卷标
     */
    private static String getHDSerial(String drive) {
        String result = "";
        try {
            File file = File.createTempFile("tmp", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);

            String vbs = "Set objFSO = CreateObject(\"Scripting.FileSystemObject\")\n"
                    + "Set colDrives = objFSO.Drives\n" + "Set objDrive = colDrives.item(\"" + drive + "\")\n"
                    + "Wscript.Echo objDrive.SerialNumber";
            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result += line;
            }
            input.close();
            file.delete();
        } catch (Exception e) {

        }
        if (result.trim().length() < 1 || result == null) {
            log.info("无磁盘ID被读取");
            result = "";
        }

        return result.trim();
    }

    /**
     * 获取CPU号,多CPU时,只取第一个
     * @return
     */
    private static String getCPUSerial() {
        String result = "";
        try {
            File file = File.createTempFile("tmp", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new FileWriter(file);

            String vbs = "On Error Resume Next \r\n\r\n" + "strComputer = \".\"  \r\n"
                    + "Set objWMIService = GetObject(\"winmgmts:\" _ \r\n"
                    + "    & \"{impersonationLevel=impersonate}!\\\\\" & strComputer & \"\\root\\cimv2\") \r\n"
                    + "Set colItems = objWMIService.ExecQuery(\"Select * from Win32_Processor\")  \r\n "
                    + "For Each objItem in colItems\r\n " + "    Wscript.Echo objItem.ProcessorId  \r\n "
                    + "    exit for  ' do the first cpu only! \r\n" + "Next                    ";

            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result += line;
            }
            input.close();
            file.delete();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        if (result.trim().length() < 1 || result == null) {
            log.info("无CPU_ID被读取");
            result = "";
        }
        return result.trim();
    }
}