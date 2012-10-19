package com.apdplat.module.security.action;

import com.apdplat.module.security.model.Position;
import com.apdplat.module.security.service.PositionService;
import com.apdplat.platform.action.ExtJSSimpleAction;
import com.apdplat.platform.util.Struts2Utils;
import javax.annotation.Resource;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Scope("prototype")
@Controller
@Namespace("/security")
public class PositionAction extends ExtJSSimpleAction<Position> {
        private String node;
        @Resource(name="positionService")
        private PositionService positionService;
        private boolean recursion=false;

        public String store(){            
            if(recursion){
                int rootId = positionService.getRootPosition().getId();
                String json=positionService.toJson(rootId,recursion);
                Struts2Utils.renderJson(json);
                
                return null;
            }
            
            return query();
        }
        @Override
        public String query(){
            //如果node为null则采用普通查询方式
            if(node==null){
                return super.query();
            }
            
            //如果指定了node则采用自定义的查询方式
            if(node.trim().startsWith("root")){
                String json=positionService.toRootJson(recursion);
                Struts2Utils.renderJson(json);
            }else{
                String[] attr=node.trim().split("-");
                if(attr.length==2){
                    int positionId=Integer.parseInt(attr[1]);
                    String json=positionService.toJson(positionId,recursion);
                    Struts2Utils.renderJson(json);                    
                }                
            }
            return null;
        }

        public void setNode(String node) {
            this.node = node;
        }

        public void setRecursion(boolean recursion) {
            this.recursion = recursion;
        }
}