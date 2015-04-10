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

package org.apdplat.module.info.service.register;

import org.apdplat.module.info.model.InfoType;
import org.apdplat.module.info.model.InfoTypeContent;
import org.apdplat.module.system.service.RegisterService;
import org.apdplat.platform.util.XMLFactory;
import org.apdplat.platform.util.XMLUtils;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 *将初始数据导入数据库
 * @author 杨尚川
 */
@Service
public class RegisteInfoType extends RegisterService<InfoType>{
    private InfoType infoType=null;
    @Override
    public void registe() {
        String xml="/data/infoType.xml";
        LOG.info("注册【"+xml+"】文件");
        LOG.info("验证【"+xml+"】文件");
        boolean pass=XMLUtils.validateXML(xml);
        if(!pass){
            LOG.info("验证没有通过，请参考dtd文件");
            return ;
        }
        LOG.info("验证通过");
        XMLFactory factory=new XMLFactory(InfoType.class);
        infoType=factory.unmarshal(RegisteInfoType.class.getResourceAsStream(xml));
        
        assembleInfoType(infoType);        
        registeInfoType(infoType);
    }

    @Override
    protected List<InfoType> getRegisteData() {
        ArrayList<InfoType> data=new ArrayList<>();
        data.add(infoType);
        return data;
    }
    /**
     * 利用递归的方式来组装树形结构
     * @param infoType 
     */
    private void assembleInfoType(InfoType infoType) {
        assembleInfoTypeContent(infoType);
        infoType.getChild().forEach(child -> {
            //重点在这行代码，默认从XML解析出来的的树是向下引用，没有向上引用
            //具体可看对应的XML配置文件即可明白
            child.setParent(infoType);
            assembleInfoType(child);
        });
    }
    /**
     * 默认从XML解析出来的的树有对内容的引用，但内容没有对树节点的引用
     * @param infoType 
     */
    private void assembleInfoTypeContent(InfoType infoType) {
        infoType.getInfoTypeContents().forEach(infoTypeContents -> {
            infoTypeContents.setInfoType(infoType);
        });
    }

    private void registeInfoType(InfoType infoType) {
        serviceFacade.create(infoType);
    }
}