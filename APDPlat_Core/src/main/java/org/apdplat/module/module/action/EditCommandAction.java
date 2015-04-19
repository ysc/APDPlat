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

import org.apdplat.module.module.model.Command;
import org.apdplat.platform.action.ExtJSSimpleAction;
import org.apdplat.module.module.service.ModuleCache;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
* 维护树形模块，对应于module.xml文件
 * 在module.xml中的数据未导入到数据库之前，可以通过修改module.xml文件的形式修改树形模块
 * 在module.xml中的数据导入到数据库之后，就只能在浏览器网页中对树形模块进行修改
 *
 * 修改命令
* @author 杨尚川
*/
@Controller
@Scope("prototype")
@RequestMapping("/module/edit-command/")
public class EditCommandAction extends ExtJSSimpleAction<Command> {

    @Override
    protected void afterSuccessPartUpdateModel(Command model) {
        //手动清空缓存
        ModuleCache.clear();
    }
}