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

import org.apdplat.module.security.model.Position;
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
public class RegistePosition extends RegisterService<Position>{
    private Position position=null;
    @Override
    public void registe() {
        String xml="/data/position.xml";
        LOG.info("注册【"+xml+"】文件");
        LOG.info("验证【"+xml+"】文件");
        boolean pass=XMLUtils.validateXML(xml);
        if(!pass){
            LOG.info("验证没有通过，请参考dtd文件");
            return ;
        }
        LOG.info("验证通过");
        XMLFactory factory=new XMLFactory(Position.class);
        position=factory.unmarshal(RegistePosition.class.getResourceAsStream(xml));
        
        assemblePosition(position);
        registePosition(position);
    }

    @Override
    protected List<Position> getRegisteData() {
        ArrayList<Position> data=new ArrayList<>();
        data.add(position);
        return data;
    }

    private void assemblePosition(Position position) {
        position.getChild().forEach(child -> {
            child.setParent(position);
            assemblePosition(child);
        });
    }

    private void registePosition(Position position) {
        serviceFacade.create(position);
    }
}