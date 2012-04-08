package com.apdplat.module.dictionary.action;

import com.apdplat.module.dictionary.model.Dic;
import com.apdplat.module.dictionary.model.DicItem;
import com.apdplat.module.dictionary.service.DicService;
import com.apdplat.platform.action.ExtJSSimpleAction;
import com.apdplat.platform.util.Struts2Utils;
import javax.annotation.Resource;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Scope("prototype")
@Controller
@Namespace("/dictionary")
public class DicItemAction extends ExtJSSimpleAction<DicItem> {
    @Resource(name = "dicService")
    private DicService dicService;
    private String node;

    /**
     * 返回数据字典目录树
     * @return 
     */
    public String store() {
        if (node == null) {
            return null;
        }
        Dic dic=null;
        if(node.trim().startsWith("root")){
            dic = dicService.getRootDic();
        }else{
            int id=Integer.parseInt(node);
            dic = dicService.getDic(id);
        }
        
        if (dic != null) {
            String json = dicService.toJson(dic);
            Struts2Utils.renderJson(json);
        }
        return null;
    }

    public void setNode(String node) {
        this.node = node;
    }
}