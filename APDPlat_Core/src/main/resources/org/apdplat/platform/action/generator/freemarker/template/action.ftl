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

package ${actionPackage};

import ${modelPackage}.${model};
import org.apdplat.platform.action.ExtJSSimpleAction;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Scope("prototype")
@Controller
@RequestMapping("/${actionNamespace}")
public class ${actionName} extends ExtJSSimpleAction<${model}> {
<#list specialCommands as specialCommand>
    /**
    *${specialCommand.chinese}
    *@return String 转向的页面
    */
    public String ${specialCommand.english}(){
        //此方法是自动生成的，请根据业务需求完善此方法
        return null;
    }
</#list>   
}