/**
 * 
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川
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

package com.apdplat.platform.generator;



import com.apdplat.module.module.model.Module;
import com.apdplat.module.module.service.ModuleParser;
import com.apdplat.module.module.service.ModuleService;
import java.util.List;

/**
 *
 * @author ysc
 */
public class WebGenerator {
    public static void generate(){            
        //不会强行覆盖JSP和JS页面，如果待生成的文件存在则会忽略生成
        List<Module> list=ModuleParser.getRootModules();
        System.out.println("--------------------------------------------------------------");
        System.out.println("--------------------------------------------------------------");
        System.out.println("模块数:"+list.size());
        System.out.println("--------------------------------------------------------------");
        int i=1;
        for(Module module : list){
            System.out.println("--------------------------------------------------------------");
            System.out.println("模块"+(i++)+"大小:"+module.getSubModules().size());
            System.out.println("--------------------------------------------------------------");
            int j=1;
            for(Module m : module.getSubModules()){
                System.out.println("    "+(j++)+":"+m.getChinese()+"("+m.getEnglish()+")");
                generate(m);
            }
            System.out.println("--------------------------------------------------------------");
        }
    }
    private static void generate(Module module){
        //如果模块不为叶子节点
        if(module.getCommands().isEmpty()){
            for(Module subModule : module.getSubModules()){
                generate(subModule);
            }
        }else{
            String path=ModuleService.getModulePath(module.getParentModule());
            System.out.println("        module: "+module.getChinese()+"("+module.getEnglish()+")"+", path: "+path);
            if(module.isDisplay()){
                JspGenerator.generate(path, module.getEnglish(), module.getChinese());
            }else{
                System.out.println("        模块不显示，不生成页面和JS");
            }
        }
        
    }
}