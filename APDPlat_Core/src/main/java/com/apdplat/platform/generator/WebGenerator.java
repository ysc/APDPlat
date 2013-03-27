/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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