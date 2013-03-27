/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.apdplat.module.security.service;

/**
 *
 * @author ysc
 */
import com.apdplat.platform.log.APDPlatLogger;
import com.apdplat.platform.util.FileUtils;
import java.util.Collection;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

public class SecurityService {
    protected static final APDPlatLogger log = new APDPlatLogger(SecurityService.class);

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
        String code = new Md5PasswordEncoder().encodePassword(newCode,"杨尚川").toUpperCase()+machineCode.length();
        return getSplitString(code);
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
            log.debug("安全检查出错",e);
        }
        return false;
    }
}