package com.apdplat.module.security.service.register;

import com.apdplat.module.security.model.Org;
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
public class RegisteOrg extends RegisterService<Org>{
    private Org org=null;
    @Override
    public void registe() {
        String xml="/data/org.xml";
        log.info("注册【"+xml+"】文件");
        log.info("验证【"+xml+"】文件");
        boolean pass=XMLUtils.validateXML(xml);
        if(!pass){
            log.info("验证没有通过，请参考dtd文件");
            return ;
        }
        log.info("验证通过");
        XMLFactory factory=new XMLFactory(Org.class);
        org=factory.unmarshal(RegisteOrg.class.getResourceAsStream(xml));
        
        assembleOrg(org);
        registeOrg(org);
    }

    @Override
    protected List<Org> getRegisteData() {
        ArrayList<Org> data=new ArrayList<>();
        data.add(org);
        return data;
    }

    private void assembleOrg(Org org) {
        for(Org child : org.getChild()){
            child.setParent(org);
            assembleOrg(child);
        }
    }

    private void registeOrg(Org org) {
        serviceFacade.create(org);
    }
}
