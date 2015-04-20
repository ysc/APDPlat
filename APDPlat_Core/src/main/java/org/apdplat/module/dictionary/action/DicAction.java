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
import org.apdplat.module.dictionary.service.DicService;
import org.apdplat.platform.action.ExtJSSimpleAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Scope("prototype")
@Controller
@RequestMapping("/dictionary/dic/")
public class DicAction extends ExtJSSimpleAction<Dic> {
    @Resource
    private DicService dicService;
    
    /**
     * 
     * 此类用来提供下拉列表服务,主要有两种下拉类型：
     * 1、普通下拉选项
     * 2、树形下拉选项
     * @param dic
     * @param tree
     * @param justCode
     * @return 返回值直接给客户端
     */
    @ResponseBody
    @RequestMapping("store.action")
    public String store(@RequestParam(required=false) String dic,
                        @RequestParam(required=false) String tree,
                        @RequestParam(required=false) String justCode){
        Dic dictionary=dicService.getDic(dic);
        if(dictionary==null){
            LOG.info("没有找到数据词典 "+dic);
            return "[]";
        }
        if("true".equals(tree)){
            String json = dicService.toStoreJson(dictionary);
            return json;
        }else{
            List<Map<String,String>> data=new ArrayList<>();
            dictionary.getDicItems().forEach(item -> {
                Map<String,String> itemMap=new HashMap<>();
                if("true".equals(justCode)){
                    itemMap.put("value", item.getCode());
                }else{
                    itemMap.put("value", item.getId().toString());
                }
                itemMap.put("text", item.getName());
                data.add(itemMap);
            });
            return toJson(data);
        }
    }
}