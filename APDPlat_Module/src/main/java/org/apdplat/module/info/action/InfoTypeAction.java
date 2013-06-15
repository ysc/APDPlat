/**
 * 
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.apdplat.module.info.action;

import org.apdplat.module.info.model.InfoType;
import org.apdplat.module.info.model.News;
import org.apdplat.module.info.service.InfoTypeService;
import org.apdplat.platform.action.ExtJSSimpleAction;
import org.apdplat.platform.criteria.Property;
import org.apdplat.platform.util.Struts2Utils;
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
        LOG.info("控制器设置语言："+lang);
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