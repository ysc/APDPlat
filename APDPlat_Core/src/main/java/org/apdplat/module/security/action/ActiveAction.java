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

import org.apdplat.module.security.service.SecurityCheck;
import org.apdplat.platform.util.FileUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 激活产品
 * @author 杨尚川
 */
@Controller
@RequestMapping("/security/active/")
public class ActiveAction{
    @ResponseBody
    @RequestMapping("buy.action")
    public String buy(){
        
        return "暂不支持在线购买";
    }
    @ResponseBody
    @RequestMapping("active.action")
    public String active(@RequestParam String licence){
        FileUtils.createAndWriteFile("/WEB-INF/classes/licences/apdplat.licence", licence);
        SecurityCheck.check();
        if(FileUtils.existsFile("/WEB-INF/licence")){
                return "您的注册码不正确，激活失败！";
        }else{
                return "激活成功，感谢您的购买！";
        }
    }
}