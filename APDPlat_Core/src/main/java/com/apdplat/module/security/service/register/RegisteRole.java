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

import com.apdplat.module.security.model.Role;
import com.apdplat.module.system.service.RegisterService;
import com.apdplat.platform.util.XMLFactory;
import com.apdplat.platform.util.XMLUtils;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 *
 * @author ysc
 */
@Service
public class RegisteRole extends RegisterService<Role>{
    private Role role=null;
    @Override
    public void registe() {
        String xml="/data/role.xml";
        log.info("注册【"+xml+"】文件");
        log.info("验证【"+xml+"】文件");
        boolean pass=XMLUtils.validateXML(xml);
        if(!pass){
            log.info("验证没有通过，请参考dtd文件");
            return ;
        }
        log.info("验证通过");
        XMLFactory factory=new XMLFactory(Role.class);
        role=factory.unmarshal(RegisteRole.class.getResourceAsStream(xml));
        
        assembleRole(role);
        registeRole(role);
    }

    @Override
    protected List<Role> getRegisteData() {
        ArrayList<Role> data=new ArrayList<Role>();
        data.add(role);
        return data;
    }

    private void assembleRole(Role role) {
        for(Role child : role.getChild()){
            child.setParent(role);
            assembleRole(child);
        }
    }

    private void registeRole(Role role) {
        serviceFacade.create(role);
    }
}