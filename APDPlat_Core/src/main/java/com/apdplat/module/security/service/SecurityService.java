package com.apdplat.module.security.service;

/**
 *
 * @author ysc
 */
import com.apdplat.platform.util.FileUtils;
import java.util.Collection;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

public class SecurityService {
    private static final Logger log = LoggerFactory.getLogger(SecurityService.class);

    public void checkSeq(String seq){
        if(StringUtils.isNotBlank(seq)){
            log.debug("机器码为："+seq);
            if(valide(seq)){
                authSuccess();
                log.debug("产品已经取得合法授权");
            }else{
                log.debug("产品没有取得授权");
                authFail(seq);
            }
        }else{
            log.debug("机器码获取失败");
            log.debug("产品没有取得授权");
            authFail(seq);
        }
    }
    private void authSuccess(){
        FileUtils.removeFile("/WEB-INF/lib/server");
        FileUtils.removeFile("/WEB-INF/licence");
    }
    private void authFail(String seq){
        FileUtils.createAndWriteFile("/WEB-INF/lib/server",seq);
        FileUtils.createAndWriteFile("/WEB-INF/licence",seq);
    }
    private String auth(String machineCode){
        String newCode="(yang-shangchuan@qq.com)["+machineCode.toUpperCase()+"](APDPlat应用级产品开发平台)";
        return new Md5PasswordEncoder().encodePassword(newCode,"杨尚川").toUpperCase()+machineCode.length();
    }
    private boolean valide(String  seq) {
        try{
            String authCode=auth(seq);
            if(StringUtils.isBlank(authCode)){
                return false;
            }
            Collection<String> licences=FileUtils.getTextFileContent("/WEB-INF/classes/licences/apdplat.licence");
            for(String no : licences){
                if(authCode.equals(no)){
                    return true;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
}