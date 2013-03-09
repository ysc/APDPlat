package com.apdplat.module.security.service;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

/**
 *此工具负责根据用户的机器码来生成注册码
 * @author ysc
 */
public class AuthorizationUtils {
    public static void main(String args[]) throws Exception {
        String code="71F5-DA7F-495E-7F70-6D47-F3E6-3DC6-349A";
        String authCode=auth(code);
        System.out.println("机器码："+code);
        System.out.println("注册码："+authCode);
    }
    public static String auth(String machineCode){
        String newCode="(yang-shangchuan@qq.com)["+machineCode.toUpperCase()+"](APDPlat应用级产品开发平台)";
        String code = new Md5PasswordEncoder().encodePassword(newCode,"杨尚川").toUpperCase()+machineCode.length();
        return getSplitString(code);
    }
    private static String getSplitString(String str){ 
        return getSplitString(str, "-", 4);
    }
    private static String getSplitString(String str, String split, int length){        
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
}
