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

package org.apdplat.module.security.service.password;

import javax.annotation.Resource;
import org.apdplat.module.security.model.User;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户密码双重加密：
 * 1、使用SHA-512算法，salt为user.getMetaData()，即：用户信息
 * 2、使用SHA-256算法，salt为saltSource.getSalt(user)，即：用户名+APDPlat应用级产品开发平台的作者是杨尚川，联系方式（邮件：ysc@apdplat.org）(QQ：281032878)
 * @author 杨尚川
 */
@Service
public class PasswordEncoder {    
    @Resource(name="saltSource")
    private SaltSource saltSource;
    private final ShaPasswordEncoder shaPasswordEncoder256 = new ShaPasswordEncoder(256);
    private final ShaPasswordEncoder shaPasswordEncoder512 = new ShaPasswordEncoder(512);

    public void setSaltSource(SaltSource saltSource) {
        this.saltSource = saltSource;
    }
    
    public String encode(String password,User user){
        return encode256(encode512(password, user), user);
    }
    private String encode512(String password,User user){
        return shaPasswordEncoder512.encodePassword(password,user.getMetaData());
    }
    private String encode256(String password,User user){
        return shaPasswordEncoder256.encodePassword(password,saltSource.getSalt(user));
    }
    public static void main(String[] args){
        User user = new User();
        user.setUsername("admin");
        user.setPassword("admin");
        
        SaltSource saltSource = new APDPlatSaltSource();
        PasswordEncoder passwordEncoder = new PasswordEncoder();
        passwordEncoder.setSaltSource(saltSource);
        
        String password = passwordEncoder.encode512(user.getPassword(), user);
        System.out.println("Step 1 use SHA-512: "+password+" length:"+password.length());
        
        password = passwordEncoder.encode256(password, user);
        System.out.println("Step 2 use SHA-256: "+password+" length:"+password.length());
    }
}