package com.apdplat.module.security.service.register;

import com.apdplat.module.security.model.Role;
import com.apdplat.module.system.service.RegisterService;
import com.apdplat.platform.util.XMLFactory;
import com.apdplat.platform.util.XMLUtils;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 *
 * @author ysc
 */
@Service
public class RegisteRole extends RegisterService<Role>{
    private Role role=null;
    @Override
    public void registe() {
        String xml="/data/role.xml";
        log.info("注册【"+xml+"】文件");
        log.info("验证【"+xml+"】文件");
        boolean pass=XMLUtils.validateXML(xml);
        if(!pass){
            log.info("验证没有通过，请参考dtd文件");
            return ;
        }
        log.info("验证通过");
        XMLFactory factory=new XMLFactory(Role.class);
        role=factory.unmarshal(RegisteRole.class.getResourceAsStream(xml));
        
        assembleRole(role);
        registeRole(role);
    }

    @Override
    protected List<Role> getRegisteData() {
        ArrayList<Role> data=new ArrayList<Role>();
        data.add(role);
        return data;
    }

    private void assembleRole(Role role) {
        for(Role child : role.getChild()){
            child.setParent(role);
            assembleRole(child);
        }
    }

    private void registeRole(Role role) {
        serviceFacade.create(role);
    }
}
