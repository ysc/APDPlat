package com.apdplat.module.dictionary.service.register;

import com.apdplat.module.dictionary.model.Dic;
import com.apdplat.module.dictionary.service.DicParser;
import com.apdplat.module.dictionary.service.DicService;
import com.apdplat.module.system.service.RegisterService;
import com.apdplat.platform.result.Page;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 *数据字典注册服务
 * @author 杨尚川
 */
@Service
public class RegisteDic extends RegisterService<Dic>{
    private List<Dic> data;
    @Resource(name = "dicService")
    private  DicService dicService;

    private Dic rootDic;
    
    /**
     * 每一次启动的时候都要检查所有的数据字典是否已经注册
     * @return 
     */
    @Override
    protected boolean shouldRegister() {
        return true;
    }
    @Override
    public void registe() {
        data=new ArrayList<Dic>();
        List<Dic> dics=DicParser.getDics();
        for(Dic dic : dics){
            registeDic(dic);
        }
    }
    private void registeDic(Dic dic){
        Page<Dic> page=serviceFacade.query(Dic.class);
        if(page.getTotalRecords()==0){
            log.info("第一次注册第一个数据字典: "+dic.getChinese());            
            serviceFacade.create(dic);
            //保存根数据字典
            rootDic=dic;
            data.add(dic);
        }else{
            log.info("以前已经注册过数据字典");
            log.info("查找出根数据字典");
            Dic root=null;
            Dic existRoot=dicService.getRootDic();
            if(existRoot!=null){
                root=existRoot;
                log.info("找到以前导入的根数据字典: "+root.getChinese());
            }else{
                log.info("没有找到以前导入的根数据字典");
                if(rootDic!=null){
                    log.info("使用本次导入的根数据字典");
                    root=rootDic;
                }
            }
            if(root!=null){
                log.info("将第一次以后的数据字典的根数据字典设置为第一次注册的根数据字典");
                for(Dic subDic : dic.getSubDics()){
                    if(hasRegisteDic(subDic)){
                        log.info("数据字典 "+subDic.getChinese()+" 在此前已经被注册过，此次忽略，检查其子数据字典");
                        registeSubDic(subDic);
                        continue;
                    }
                    subDic.setParentDic(root);
                    log.info("注册后续数据字典: "+subDic.getChinese());
                    serviceFacade.create(subDic);
                    data.add(subDic);
                }
            }else{
                log.info("没有找到根数据字典，注册失败！");
            }
        }
    }
    private void registeSubDic(Dic dic){
        for(Dic sub : dic.getSubDics()){
            if(hasRegisteDic(sub)){
                log.info("数据字典 "+sub.getChinese()+" 在此前已经被注册过，此次忽略，检查其子数据字典");
                registeSubDic(sub);
            }else{
                log.info("注册后续数据字典: "+sub.getChinese());
                //重新从数据库中查询出数据字典，参数中的数据字典是从XML中解析出来的
                sub.setParentDic(dicService.getDic(dic.getEnglish()));
                serviceFacade.create(sub);
                data.add(sub);
            }
        }
    }
    private boolean hasRegisteDic(Dic dic){
        Dic existsDic=dicService.getDic(dic.getEnglish());
        if(existsDic==null){
            return false;
        }
        return true;
    }
    @Override
    public List<Dic> getRegisteData() {
        return data;
    }
}
