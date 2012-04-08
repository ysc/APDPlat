package com.apdplat.module.info.service.register;

import com.apdplat.module.info.model.InfoType;
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
public class RegisteInfoType extends RegisterService<InfoType>{
    private InfoType infoType=null;
    @Override
    public void registe() {
        String xml="/data/infoType.xml";
        log.info("注册【"+xml+"】文件");
        log.info("验证【"+xml+"】文件");
        boolean pass=XMLUtils.validateXML(xml);
        if(!pass){
            log.info("验证没有通过，请参考dtd文件");
            return ;
        }
        log.info("验证通过");
        XMLFactory factory=new XMLFactory(InfoType.class);
        infoType=factory.unmarshal(RegisteInfoType.class.getResourceAsStream(xml));
        
        assembleInfoType(infoType);
        registeInfoType(infoType);
    }

    @Override
    protected List<InfoType> getRegisteData() {
        ArrayList<InfoType> data=new ArrayList<InfoType>();
        data.add(infoType);
        return data;
    }

    private void assembleInfoType(InfoType infoType) {
        for(InfoType child : infoType.getChild()){
            child.setParent(infoType);
            assembleInfoType(child);
        }
    }

    private void registeInfoType(InfoType infoType) {
        serviceFacade.create(infoType);
    }
}
