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

package com.apdplat.module.security.service.register;

import com.apdplat.module.security.model.User;
import com.apdplat.module.security.service.PasswordEncoder;
import com.apdplat.module.system.service.RegisterService;
import com.apdplat.platform.result.Page;
import com.apdplat.platform.util.XMLUtils;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 *
 * @author ysc
 */
@Service
public class RegisteUser extends RegisterService<User>{
    @Resource(name="registeOrg")
    protected RegisteOrg registeOrg;
    @Resource(name="registeRole")
    protected RegisteRole registeRole;

    @Override
    protected void registe() {
        String xml="/data/user.xml";
        log.info("注册【"+xml+"】文件");
        log.info("验证【"+xml+"】文件");
        boolean pass=XMLUtils.validateXML(xml);
        if(!pass){
            log.info("验证没有通过，请参考dtd文件");
            return ;
        }
        log.info("验证通过");
        Page<User> page=Page.newInstance(User.class, RegisteUser.class.getResourceAsStream(xml));
        if(page!=null){
            for(User user : page.getModels()){
                user.setPassword(PasswordEncoder.encode(user.getPassword(), user));
                user.setOrg(registeOrg.getRegisteData().get(0));
                user.addRole(registeRole.getRegisteData().get(0).getChild().get(0));
                serviceFacade.create(user);
            }
        }
    }
}