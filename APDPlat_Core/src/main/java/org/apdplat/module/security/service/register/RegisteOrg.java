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

import org.apdplat.module.security.model.Org;
import org.apdplat.module.system.service.RegisterService;
import org.apdplat.platform.util.XMLFactory;
import org.apdplat.platform.util.XMLUtils;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 *
 * @author 杨尚川
 */
@Service
public class RegisteOrg extends RegisterService<Org>{
    private Org org=null;
    @Override
    public void registe() {
        String xml="/data/org.xml";
        LOG.info("注册【"+xml+"】文件");
        LOG.info("验证【"+xml+"】文件");
        boolean pass=XMLUtils.validateXML(xml);
        if(!pass){
            LOG.info("验证没有通过，请参考dtd文件");
            return ;
        }
        LOG.info("验证通过");
        XMLFactory factory=new XMLFactory(Org.class);
        org=factory.unmarshal(RegisteOrg.class.getResourceAsStream(xml));
        
        assembleOrg(org);
        registeOrg(org);
    }

    @Override
    protected List<Org> getRegisteData() {
        ArrayList<Org> data=new ArrayList<>();
        data.add(org);
        return data;
    }

    private void assembleOrg(Org org) {
        org.getChild().forEach(child -> {
            child.setParent(org);
            assembleOrg(child);
        });
    }

    private void registeOrg(Org org) {
        serviceFacade.create(org);
    }
}