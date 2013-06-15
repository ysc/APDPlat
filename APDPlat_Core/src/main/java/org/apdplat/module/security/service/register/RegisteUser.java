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

package org.apdplat.module.security.service.register;

import org.apdplat.module.security.model.User;
import org.apdplat.module.security.service.PasswordEncoder;
import org.apdplat.module.system.service.RegisterService;
import org.apdplat.platform.result.Page;
import org.apdplat.platform.util.XMLUtils;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 *
 * @author 杨尚川
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
        LOG.info("注册【"+xml+"】文件");
        LOG.info("验证【"+xml+"】文件");
        boolean pass=XMLUtils.validateXML(xml);
        if(!pass){
            LOG.info("验证没有通过，请参考dtd文件");
            return ;
        }
        LOG.info("验证通过");
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