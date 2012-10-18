package com.apdplat.module.security.service.register;

import com.apdplat.module.security.model.Position;
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
public class RegistePosition extends RegisterService<Position>{
    private Position position=null;
    @Override
    public void registe() {
        String xml="/data/position.xml";
        log.info("注册【"+xml+"】文件");
        log.info("验证【"+xml+"】文件");
        boolean pass=XMLUtils.validateXML(xml);
        if(!pass){
            log.info("验证没有通过，请参考dtd文件");
            return ;
        }
        log.info("验证通过");
        XMLFactory factory=new XMLFactory(Position.class);
        position=factory.unmarshal(RegistePosition.class.getResourceAsStream(xml));
        
        assemblePosition(position);
        registePosition(position);
    }

    @Override
    protected List<Position> getRegisteData() {
        ArrayList<Position> data=new ArrayList<>();
        data.add(position);
        return data;
    }

    private void assemblePosition(Position position) {
        for(Position child : position.getChild()){
            child.setParent(position);
            assemblePosition(child);
        }
    }

    private void registePosition(Position position) {
        serviceFacade.create(position);
    }
}
