package com.apdplat.module.security.service.register;

import com.apdplat.module.security.model.Role;
import com.apdplat.module.system.service.RegisterService;
import com.apdplat.platform.result.Page;
import com.apdplat.platform.util.XMLUtils;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 *
 * @author ysc
 */
@Service
public class RegisteRole extends RegisterService<Role>{
    private List<Role> data=null;

    @Override
    protected void registe() {
        String xml="/data/role.xml";
        log.info("注册【"+xml+"】文件");
        log.info("验证【"+xml+"】文件");
        boolean pass=XMLUtils.validateXML(xml);
        if(!pass){
            log.info("验证没有通过，请参考dtd文件");
            return ;
        }
        log.info("验证通过");
        Page<Role> page=Page.newInstance(Role.class, RegisteRole.class.getResourceAsStream(xml));
        
        if(page!=null){
            data=page.getModels();
            for(Role role : page.getModels()){
                serviceFacade.create(role);
            }
        }
    }
    @Override
    protected List<Role> getRegisteData() {
        return data;
    }
}
