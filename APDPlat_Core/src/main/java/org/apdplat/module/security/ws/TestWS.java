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
import org.apdplat.platform.log.APDPlatLogger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 *测试WEB SERVICE是否正常
 * @author 杨尚川
 */
@Service
public class TestWS {
    protected final APDPlatLogger log = new APDPlatLogger(getClass());
    @Resource(name="userServiceClient")
    private UserService userService;
    //@PostConstruct
    public void testws(){
        new Thread(){            
            @Override
            public void run(){  
                try{
                    Thread.sleep(120000);
                }catch(Exception e){
                    e.printStackTrace();
                }
                log.info("开始检查web service服务端和客户端是否正常");
                log.info("userService.login(\"admin\", \"admin\")： "+userService.login("admin", "admin"));
                User user=userService.getUserInfo("admin", "admin");
                if(user!=null){
                    log.info("user.getUsername()： "+user.getUsername());
                    log.info("user.getPassword()： "+user.getPassword());
                }
                
                log.info("userService.login(\"admin\", \"123456\")： "+userService.login("admin", "123456"));
                user=userService.getUserInfo("admin", "123456");
                if(user!=null){
                    log.info("user.getUsername()： "+user.getUsername());
                    log.info("user.getPassword()： "+user.getPassword());
                }
                
                log.info("userService.login(\"administrator\", \"123456\")： "+userService.login("administrator", "123456"));
                user=userService.getUserInfo("administrator", "123456");
                if(user!=null){
                    log.info("user.getUsername()： "+user.getUsername());
                    log.info("user.getPassword()： "+user.getPassword());
                }
                log.info("检查完毕");
            }
        }.start();
    }
}