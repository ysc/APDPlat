package com.apdplat.module.security.action;

import com.apdplat.module.security.service.SecurityCheck;
import com.apdplat.platform.action.DefaultAction;
import com.apdplat.platform.util.FileUtils;
import com.apdplat.platform.util.Struts2Utils;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 *
 * @author ysc
 */
@Scope("prototype")
@Controller
@Namespace("/security")
public class ActiveAction extends DefaultAction{
    private String licence;
    
    public String buy(){
        
        return null;
    }
    public String active(){
        FileUtils.createAndWriteFile("/WEB-INF/classes/licences/apdplat.licence", licence);
        SecurityCheck.check();
        if(FileUtils.existsFile("/WEB-INF/licence")){
                Struts2Utils.renderText("您的注册码不正确，激活失败！");
        }else{
                Struts2Utils.renderText("激活成功，感谢您的购买！");
        }
        return null;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }
}
