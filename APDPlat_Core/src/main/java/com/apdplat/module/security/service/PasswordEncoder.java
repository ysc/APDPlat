package com.apdplat.module.security.service;

import com.apdplat.module.security.model.User;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

/**
 *
 * @author ysc
 */
public class PasswordEncoder {
    public static String encode(String password,User user){
        return new Md5PasswordEncoder().encodePassword(password,user.getMetaData());
    }
}
