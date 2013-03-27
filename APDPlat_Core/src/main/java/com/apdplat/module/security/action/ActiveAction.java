/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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