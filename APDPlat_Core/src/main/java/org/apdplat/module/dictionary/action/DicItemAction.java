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
import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Scope("prototype")
@Controller
@RequestMapping("/dictionary")
public class DicItemAction extends ExtJSSimpleAction<DicItem> {
    @Resource
    private DicService dicService;

    /**
     * 返回数据字典目录树
     * @param node
     * @return 
     */
    @ResponseBody
    @RequestMapping("/dic-item!store.action")
    public String store(@RequestParam(required=false) String node) {
        if (node == null) {
            return "[]";
        }
        Dic dic=null;
        if(node.trim().startsWith("root")){
            dic = dicService.getRootDic();
        }else{
            int id=Integer.parseInt(node);
            dic = dicService.getDic(id);
        }
        
        if (dic != null) {
            String json = dicService.toJson(dic);
            return json;
        }
        return "[]";
    }
    @ResponseBody
    @RequestMapping("/dic-item!query.action")
    public String query(@RequestParam(required=false) Integer start,
                        @RequestParam(required=false) Integer limit,
                        @RequestParam(required=false) String propertyCriteria,
                        @RequestParam(required=false) String orderCriteria,
                        @RequestParam(required=false) String queryString,
                        @RequestParam(required=false) String search){
        super.setStart(start);
        super.setLimit(limit);
        super.setPropertyCriteria(propertyCriteria);
        super.setOrderCriteria(orderCriteria);
        super.setQueryString(queryString);
        super.setSearch("true".equals(search));
        return super.query();
    }
    @ResponseBody
    @RequestMapping("/dic-item!retrieve.action")
    public String retrieve(@ModelAttribute DicItem model) {
        super.model = model;
        return super.retrieve();
    }
    @ResponseBody
    @RequestMapping("/dic-item!delete.action")
    public String delete(@RequestParam String ids) {
        super.setIds(ids);
        return super.delete();
    }
    @ResponseBody
    @RequestMapping("/dic-item!create.action")
    public String create(@ModelAttribute DicItem model) {
        super.model = model;
        return super.create();
    }
    @ResponseBody
    @RequestMapping("/dic-item!updatePart.action")
    public String updatePart(@ModelAttribute DicItem model) {
        super.model = model;
        return super.updatePart();
    }
}