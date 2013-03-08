package com.apdplat.module.info.service.register;

import com.apdplat.module.info.model.InfoType;
import com.apdplat.module.info.model.InfoTypeContent;
import com.apdplat.module.system.service.RegisterService;
import com.apdplat.platform.util.XMLFactory;
import com.apdplat.platform.util.XMLUtils;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 *将初始数据导入数据库
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
        ArrayList<InfoType> data=new ArrayList<>();
        data.add(infoType);
        return data;
    }
    /**
     * 利用递归的方式来组装树形结构
     * @param infoType 
     */
    private void assembleInfoType(InfoType infoType) {
        assembleInfoTypeContent(infoType);
        for(InfoType child : infoType.getChild()){
            //重点在这行代码，默认从XML解析出来的的树是向下引用，没有向上引用
            //具体可看对应的XML配置文件即可明白
            child.setParent(infoType);
            assembleInfoType(child);
        }
    }
    /**
     * 默认从XML解析出来的的树有对内容的引用，但内容没有对树节点的引用
     * @param infoType 
     */
    private void assembleInfoTypeContent(InfoType infoType) {
        for(InfoTypeContent infoTypeContents : infoType.getInfoTypeContents()){
            infoTypeContents.setInfoType(infoType);
        }
    }

    private void registeInfoType(InfoType infoType) {
        serviceFacade.create(infoType);
    }
}
