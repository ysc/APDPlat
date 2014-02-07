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
import java.util.Collection;
import org.apache.commons.lang.StringUtils;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

public class SecurityService {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(SecurityService.class);

    public void checkSeq(String seq){
        if(StringUtils.isNotBlank(seq)){
            LOG.debug("机器码为："+seq);
            if(valide(seq)){
                authSuccess();
                LOG.debug("产品已经取得合法授权");
            }else{
                LOG.debug("产品没有取得授权");
                authFail(seq);
            }
        }else{
            LOG.debug("机器码获取失败");
            LOG.debug("产品没有取得授权");
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
            LOG.debug("安全检查出错",e);
        }
        return false;
    }
}