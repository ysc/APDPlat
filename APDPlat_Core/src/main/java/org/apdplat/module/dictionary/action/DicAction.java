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

package org.apdplat.module.dictionary.action;

import org.apdplat.module.dictionary.model.Dic;
import org.apdplat.module.dictionary.model.DicItem;
import org.apdplat.module.dictionary.service.DicService;
import org.apdplat.platform.action.ExtJSSimpleAction;
import org.apdplat.platform.util.Struts2Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Scope("prototype")
@Controller
@Namespace("/dictionary")
public class DicAction extends ExtJSSimpleAction<Dic> {
    @Resource(name = "dicService")
    private DicService dicService;
    private String dic;
    private String tree;
    private boolean justCode;
    
    /**
     * 
     * 此类用来提供下列列表服务,主要有两中下列类型：
     * 1、普通下拉选项
     * 2、树形下拉选项
     * 
     */
    public String store(){
        Dic dictionary=dicService.getDic(dic);
        if(dictionary==null){
            LOG.info("没有找到数据词典 "+dic);
            return null;
        }
        if("true".equals(tree)){
            String json = dicService.toStoreJson(dictionary);
            Struts2Utils.renderJson(json);
        }else{
            List<Map<String,String>> data=new ArrayList<>();
            for(DicItem item : dictionary.getDicItems()){
                Map<String,String> map=new HashMap<>();
                if(justCode){
                    map.put("value", item.getCode());
                }else{
                    map.put("value", item.getId().toString());
                }
                map.put("text", item.getName());
                data.add(map);
            }
            Struts2Utils.renderJson(data);
        }
        return null;
    }

    public void setJustCode(boolean justCode) {
        this.justCode = justCode;
    }

    public void setTree(String tree) {
        this.tree = tree;
    }

    public void setDic(String dic) {
        this.dic = dic;
    }
}