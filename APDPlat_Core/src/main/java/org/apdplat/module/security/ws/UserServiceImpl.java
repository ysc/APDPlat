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

package org.apdplat.module.security.ws;

import org.apdplat.module.security.model.User;
import org.apdplat.module.security.service.password.PasswordEncoder;
import org.apdplat.module.security.service.UserDetailsServiceImpl;
import org.apdplat.platform.log.APDPlatLogger;
import javax.annotation.Resource;
import javax.jws.WebService;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
/**
 * 用户认证服务实现
 * @author 杨尚川
 */
@Service
@WebService(endpointInterface = "org.apdplat.module.security.ws.UserService")
public class UserServiceImpl implements UserService{
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(UserServiceImpl.class);
    @Resource(name = "userDetailsServiceImpl")
    private UserDetailsServiceImpl userDetailsServiceImpl;
    @Resource(name="passwordEncoder")
    private PasswordEncoder passwordEncoder;
    
    @Override
    public String login(String username, String password) {
        try{
            User user=(User)userDetailsServiceImpl.loadUserByUsername(username);
            password=passwordEncoder.encode(password, user);
            if(password.equals(user.getPassword())){
                return "认证成功";
            }else{
                return "密码不正确";
            }
        }catch(UsernameNotFoundException | DataAccessException e){
            return e.getMessage();
        }
    }

    @Override
    public User getUserInfo(String username, String password) {
        try{
            User user=(User)userDetailsServiceImpl.loadUserByUsername(username);
            if(user!=null){
                password=passwordEncoder.encode(password, user);
                if(password.equals(user.getPassword())){
                    return user;
                }
            }
        }catch(UsernameNotFoundException | DataAccessException e){
            LOG.info("没有获取到用户信息："+username);
        }
        return null;
    }
    
}