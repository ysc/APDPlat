package com.apdplat.module.security.action;

import com.apdplat.module.security.model.Org;
import com.apdplat.module.security.service.OrgService;
import com.apdplat.platform.action.ExtJSSimpleAction;
import com.apdplat.platform.util.Struts2Utils;
import javax.annotation.Resource;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Scope("prototype")
@Controller
@Namespace("/security")
public class OrgAction extends ExtJSSimpleAction<Org> {
        private String node;
        @Resource(name="orgService")
        private OrgService orgService;

        public String store(){
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
                String json=orgService.toRootJson();
                Struts2Utils.renderJson(json);
            }else{
                int id=Integer.parseInt(node.trim());
                String json=orgService.toJson(id);
                Struts2Utils.renderJson(json);
            }
            return null;
        }

        public void setNode(String node) {
            this.node = node;
        }
}
