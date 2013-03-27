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

package com.apdplat.module.info.action;

import com.apdplat.module.info.model.InfoType;
import com.apdplat.module.info.model.News;
import com.apdplat.module.info.service.InfoTypeService;
import com.apdplat.platform.action.ExtJSSimpleAction;
import com.apdplat.platform.criteria.Property;
import com.apdplat.platform.util.Struts2Utils;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Scope("prototype")
@Controller
@Namespace("/info")
public class InfoTypeAction extends ExtJSSimpleAction<InfoType> {

    private String node;
    @Resource(name = "infoTypeService")
    private InfoTypeService infoTypeService;
    private String lang = "zh";
    
    //setInfoTypeName依赖于setLang，所以在创建的时候没有办法确保顺序
    //所以要强制指定
    @Override
    protected void assemblyModelForCreate(InfoType model) {
        model.forceSpecifyLanguageForCreate(lang);
    }
    @Override
    protected void assemblyModelForPartUpdate(List<Property> properties) {
        for(Property property : properties){
            if("lang".equals(property.getName())){
                properties.remove(property);
            }
        }
    }
    //修改模型的时候，在修改内容之前先设置语言
    @Override
    protected void old(InfoType model) {
        log.info("控制器设置语言："+lang);
        model.setLang(lang);
    }

    public String store() {
        return query();
    }

    @Override
    protected void afterRender(Map map, InfoType obj) {
        retrieveAfterRender(map, obj);
    }

    @Override
    protected void retrieveAfterRender(Map map, InfoType obj) {
        if (obj.getParent() != null) {
            obj.getParent().setLang(lang);
            map.put("parent_infoTypeName", obj.getParent().getInfoTypeName());
        }
        obj.setLang(lang);
        map.put("infoTypeName", obj.getInfoTypeName());
        map.remove("parent");
    }

    @Override
    public String query() {
        //如果node为null则采用普通查询方式
        if (node == null) {
            return super.query();
        }
        //如果指定了node则采用自定义的查询方式
        if ("root".equals(node.trim())) {
            String json = infoTypeService.toRootJson(lang);
            Struts2Utils.renderJson(json);
        } else {
            int id = Integer.parseInt(node.trim());
            String json = infoTypeService.toJson(id, lang);
            Struts2Utils.renderJson(json);
        }
        return null;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}