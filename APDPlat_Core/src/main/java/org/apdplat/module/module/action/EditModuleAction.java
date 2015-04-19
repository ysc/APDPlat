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

package org.apdplat.module.module.action;

import org.apdplat.module.module.model.Module;
import org.apdplat.module.module.service.ModuleService;
import org.apdplat.module.module.service.ModuleCache;
import org.apdplat.platform.action.ExtJSSimpleAction;
import javax.annotation.Resource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
* 维护树形模块，对应于module.xml文件
 * 在module.xml中的数据未导入到数据库之前，可以通过修改module.xml文件的形式修改树形模块
 * 在module.xml中的数据导入到数据库之后，就只能在浏览器网页中对树形模块进行修改
 *
 * 修改模块
* @author 杨尚川
*/
@Controller
@Scope("prototype")
@RequestMapping("/module/edit-module/")
public class EditModuleAction extends ExtJSSimpleAction<Module> {
        @Resource
        private ModuleService moduleService;

        @ResponseBody
        @RequestMapping("store.action")
        public String store(@RequestParam(required=false) String node){
            if(node==null){
                return "[]";
            }
            if(node.trim().startsWith("root")){
                String json=moduleService.toRootJsonForEdit();
                return json;
            }
            
            if(node.contains("-")){
                try{
                    String[] temp=node.split("-");
                    int id=Integer.parseInt(temp[1]);
                    Module module=moduleService.getModule(id);
                    String json=moduleService.toJsonForEdit(module);
                    return json;
                }catch(Exception e){
                    LOG.error("获取根模块出错",e);
                }
            }
            
            return "[]";
        }
        @Override
        protected void afterSuccessPartUpdateModel(Module model) {
            //手动清空缓存
            ModuleCache.clear();
        }
}