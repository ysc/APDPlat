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

package com.apdplat.module.security.ws;

import com.apdplat.module.security.model.User;
import com.apdplat.platform.log.APDPlatLogger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 *测试WEB SERVICE是否正常
 * @author ysc
 */
@Service
public class TestWS {
    protected final APDPlatLogger log = new APDPlatLogger(getClass());
    @Resource(name="userServiceClient")
    private UserService userService;
    @PostConstruct
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