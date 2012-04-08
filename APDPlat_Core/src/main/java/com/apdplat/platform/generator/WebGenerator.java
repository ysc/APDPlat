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
