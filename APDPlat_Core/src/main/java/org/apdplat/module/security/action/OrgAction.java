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
@RequestMapping("/security/org/")
public class OrgAction extends ExtJSSimpleAction<Org> {
        @Resource
        private OrgService orgService;

        @ResponseBody
        @RequestMapping("store.action")
        public String store(@RequestParam(required=false) String node){
            if(node==null){
                return "[]";
            }
            if(node.trim().startsWith("root")){
                String json=orgService.toRootJson();
                return json;
            }else{
                int id=Integer.parseInt(node.trim());
                String json=orgService.toJson(id);
                return json;
            }
        }
}