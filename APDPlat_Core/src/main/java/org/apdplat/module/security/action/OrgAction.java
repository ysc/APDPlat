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

package org.apdplat.module.security.action;

import org.apdplat.module.security.model.Org;
import org.apdplat.module.security.service.OrgService;
import org.apdplat.platform.action.ExtJSSimpleAction;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Scope("prototype")
@RequestMapping("/security")
public class OrgAction extends ExtJSSimpleAction<Org> {
        @Resource(name="orgService")
        private OrgService orgService;

        @ResponseBody
        @RequestMapping({"/org!query.action","/org!store.action"})
        public String query(@RequestParam(required=false) String node,
                            @RequestParam(required=false) Integer start,
                            @RequestParam(required=false) Integer limit,
                            @RequestParam(required=false) String propertyCriteria,
                            @RequestParam(required=false) String orderCriteria,
                            @RequestParam(required=false) String queryString,
                            @RequestParam(required=false) String search){
            //如果node为null则采用普通查询方式
            if(node==null){
                super.setStart(start);
                super.setLimit(limit);
                super.setPropertyCriteria(propertyCriteria);
                super.setOrderCriteria(orderCriteria);
                super.setQueryString(queryString);
                super.setSearch("true".equals(search));
                return super.query();
            }
            //如果指定了node则采用自定义的查询方式
            if("root".startsWith(node.trim())){
                String json=orgService.toRootJson();
                return json;
            }else{
                int id=Integer.parseInt(node.trim());
                String json=orgService.toJson(id);
                return json;
            }
        }
}