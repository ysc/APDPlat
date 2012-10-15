package com.apdplat.module.security.service;

import com.apdplat.module.system.service.SystemListener;
import com.apdplat.platform.util.ConvertUtils;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.NetFlags;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.Sigar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *在Linux平台上生成机器码
 * @author ysc
 */
public class LinuxSequenceService  implements SequenceService{
    private static final Logger log = LoggerFactory.getLogger(LinuxSequenceService.class);

    @Override
    public String getSequence() {
        try {
            File dir = new File(SystemListener.getContextPath(), "WEB-INF/lib/amd64-linux.so");
            System.out.println("linux lib : "+dir.getAbsolutePath());
            
            System.load(dir.getAbsolutePath());
            Set<String> result = new HashSet<String>();
            Sigar sigar = new Sigar();
            String[] ifaces = sigar.getNetInterfaceList();
            for (int i = 0; i < ifaces.length; i++) {
                NetInterfaceConfig cfg = sigar.getNetInterfaceConfig(ifaces[i]);
                if (NetFlags.LOOPBACK_ADDRESS.equals(cfg.getAddress()) || (cfg.getFlags() & NetFlags.IFF_LOOPBACK) != 0
                        || NetFlags.NULL_HWADDR.equals(cfg.getHwaddr())) {
                    continue;
                }
                String mac = cfg.getHwaddr();
                result.add(mac);
                log.debug("mac: " + mac);
            }
            if(result.size()<1){
                return null;
            }
            Properties props = System.getProperties();
            String javaVersion = props.getProperty("java.version");
            result.add(javaVersion);
            log.debug("Java的运行环境版本：    " + javaVersion);
            String javaVMVersion = props.getProperty("java.vm.version");
            result.add(javaVMVersion);
            log.debug("Java的虚拟机实现版本：    " + props.getProperty("java.vm.version"));
            String osVersion = props.getProperty("os.version");
            result.add(osVersion);
            log.debug("操作系统的版本：    " + props.getProperty("os.version"));

            Mem mem = sigar.getMem();
            // 内存总量
            String totalMem = mem.getTotal() / 1024L + "K av";
            log.debug("内存总量:    " + totalMem);
            result.add(totalMem);

            log.debug("result:    " + result);
            String machineCode = getMD5(result.toString());

            return machineCode;
        } catch (Throwable ex) {
            log.error("生成LINUX机器码失败", ex);
        }
        return null;
    }
    /**
     * 对一段String生成MD5加密信息
     * @param message 要加密的String
     * @return 生成的MD5信息
     */
    private String getMD5(String message) {
        message += "{apdplat}";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            log.debug("MD5摘要长度：" + md.getDigestLength());
            byte[] b = md.digest(message.getBytes("utf-8"));
            String md5 = ConvertUtils.byte2HexString(b)+message.length();
            return getSplitString(md5);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    private String getSplitString(String str){ 
        return getSplitString(str, "-", 4);
    }
    private String getSplitString(String str, String split, int length){        
        int len=str.length();
        StringBuilder temp=new StringBuilder();
        for(int i=0;i<len;i++){
            if(i%length==0 && i>0){
                temp.append(split);
            }
            temp.append(str.charAt(i));
        }
        String[] attrs=temp.toString().split(split);
        StringBuilder finalMachineCode=new StringBuilder();
        for(String attr : attrs){
            if(attr.length()==length){
                finalMachineCode.append(attr).append(split);
            }
        }
        String result=finalMachineCode.toString().substring(0, finalMachineCode.toString().length()-1);
        return result;
    }
    public static void main(String[] args) {
        LinuxSequenceService s = new LinuxSequenceService();
        String seq = s.getSequence();
        System.out.println(seq);
    }
    
}
