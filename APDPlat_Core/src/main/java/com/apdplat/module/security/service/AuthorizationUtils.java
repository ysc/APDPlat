package com.apdplat.module.security.service;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

/**
 *此工具负责根据用户的机器码来生成注册码
 * @author ysc
 */
public class AuthorizationUtils {
    public static void main(String args[]) throws Exception {
        String code="149AFD5B103FBA24CB52DC77E3A7F8A135";
        String authCode=auth(code);
        System.out.println("机器码："+code);
        System.out.println("注册码："+authCode);
    }
    public static String auth(String machineCode){
        String newCode="(yang-shangchuan@qq.com)["+machineCode.toUpperCase()+"](APDPlat应用级产品开发平台)";
        return new Md5PasswordEncoder().encodePassword(newCode,"杨尚川").toUpperCase()+machineCode.length();
    }
}
