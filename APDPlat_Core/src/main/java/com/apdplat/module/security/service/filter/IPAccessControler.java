package com.apdplat.module.security.service.filter;

import com.apdplat.platform.util.FileUtils;
import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author ysc
 */
public class IPAccessControler {
    private Collection<String> allow;
    private Collection<String> deny;

    public IPAccessControler() {
        reInit();
    }
    public final void reInit(){
        allow=FileUtils.getTextFileContent("/WEB-INF/ip/allow.txt");
        deny=FileUtils.getTextFileContent("/WEB-INF/ip/deny.txt");
    }

    public boolean deny(HttpServletRequest request){
        if(request==null){
            return false;
        }
        try{
            String ip = getIpAddr(request);

            if (hasMatch(ip, deny)) {
                return true;
            }

            if (!allow.isEmpty() && !hasMatch(ip, allow)) {
                return true;
            }
        }catch(Exception e){}
        return false;
    }

    private boolean hasMatch(String ip, Collection<String> regExps) {
        for (String regExp : regExps) {
            try{
                if (ip.matches(regExp)) {
                    return true;
                }
            }catch(Exception e){}
        }

        return false;
    }

    private String getIpAddr(HttpServletRequest request){        
        String ipString = request.getHeader("x-forwarded-for");
        if (StringUtils.isBlank(ipString) || "unknown".equalsIgnoreCase(ipString)) {
            ipString = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ipString) || "unknown".equalsIgnoreCase(ipString)) {
            ipString = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ipString) || "unknown".equalsIgnoreCase(ipString)) {
            ipString = request.getRemoteAddr();
        }

        // 多个路由时，取第一个非unknown的ip
        final String[] arr = ipString.split(",");
        for (final String str : arr) {
            if (!"unknown".equalsIgnoreCase(str)) {
                ipString = str;
                break;
            }
        }

        return ipString;
    }
    public static void main(String[] args){
        System.out.println("127.0.0.1".matches("127.0.*.*"));
    }
}