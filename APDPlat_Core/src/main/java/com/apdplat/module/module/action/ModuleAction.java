package com.apdplat.module.module.action;

import com.apdplat.module.module.model.Module;
import com.apdplat.module.module.service.ModuleService;
import com.apdplat.platform.action.ExtJSSimpleAction;
import com.apdplat.platform.util.Struts2Utils;
import javax.annotation.Resource;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
/**
* 为树形模块导航菜单服务
* @author 杨尚川
*/
@Controller
@Scope("prototype")
@Namespace("/module")
public class ModuleAction extends ExtJSSimpleAction<Module> {
        @Resource(name="moduleService")
        private ModuleService moduleService;
        private String node;
        private boolean privilege=false;
        private boolean recursion=false;
        @Override
        public String query(){
            if(node==null){
                return super.query();
            }
            Module module=null;
            if(node.contains("-")){
                String[] temp=node.split("-");
                int id=Integer.parseInt(temp[1]);
                module=moduleService.getModule(id);
            }else if(node.trim().startsWith("root")){
                module=moduleService.getRootModule();
            }
            if(module!=null){
                String json="";
                if(privilege){
                    json=moduleService.toJsonForPrivilege(module);
                }else{
                    json=moduleService.toJsonForUser(module,recursion);
                }
                Struts2Utils.renderJson(json);
            }
            return null;
        }

        public void setPrivilege(boolean privilege) {
            this.privilege = privilege;
        }

        public void setNode(String node) {
            this.node = node;
        }
}
