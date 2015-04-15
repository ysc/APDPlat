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

package org.apdplat.platform.generator;



import org.apdplat.module.module.model.Module;
import org.apdplat.module.module.service.ModuleParser;
import org.apdplat.module.module.service.ModuleService;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author 杨尚川
 */
public class WebGenerator {
    /**
     * 生成所有模块对应的JSP和JS
     */
    public static void generate(){
        generate(null);
    }
    /**
     * 生成指定模块的JSP和JS
     * 如果没有指定，则生成所有模块对应的JSP和JS
     * @param generateModules 顶级模块英文名称
     */
    public static void generate(Set<String> generateModules){            
        //不会强行覆盖JSP和JS页面，如果待生成的文件存在则会忽略生成
        List<Module> list=ModuleParser.getRootModules();
        System.out.println("--------------------------------------------------------------");
        System.out.println("--------------------------------------------------------------");
        System.out.println("分割模块数:"+list.size());
        System.out.println("--------------------------------------------------------------");
        AtomicInteger i=new AtomicInteger();
        list.forEach(module -> {
            System.out.println("--------------------------------------------------------------");
            System.out.println("分割模块" + i.incrementAndGet() + "包含模块数目:" + module.getSubModules().size());
            System.out.println("--------------------------------------------------------------");
            AtomicInteger j=new AtomicInteger();
            module.getSubModules().forEach(m -> {
                if(generateModules!=null && !generateModules.contains(m.getEnglish())){
                    System.out.println("忽略生成模块【"+m.getEnglish()+"】的JSP和JS文件");
                    return;
                }
                System.out.println("    "+j.incrementAndGet()+":"+m.getChinese()+"("+m.getEnglish()+")");
                generateForModule(m);
            });
            System.out.println("--------------------------------------------------------------");
        });
    }
    private static void generateForModule(Module module){
        //如果模块不为叶子节点
        if(module.getCommands().isEmpty()){
            module.getSubModules().forEach(subModule -> {
                generateForModule(subModule);
            });
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