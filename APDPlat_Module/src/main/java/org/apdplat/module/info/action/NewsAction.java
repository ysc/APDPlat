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
import org.apdplat.platform.action.converter.DateTypeConverter;
import org.apdplat.platform.criteria.Operator;
import org.apdplat.platform.criteria.Property;
import org.apdplat.platform.criteria.PropertyCriteria;
import org.apdplat.platform.criteria.PropertyEditor;
import org.apdplat.platform.criteria.PropertyType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Scope("prototype")
@Controller
@RequestMapping("/info/news/")
public class NewsAction extends ExtJSSimpleAction<News> {
    //setTitle和setContent依赖于setLang，所以在创建的时候没有办法确保顺序
    //所以要强制指定
    @Override
    protected void assemblyModelForCreate(News model) {
        String lang = getRequest().getParameter("lang");
        model.forceSpecifyLanguageForCreate(lang);
    }
    @Override
    protected void assemblyModelForPartUpdate(List<Property> properties) {
        properties.forEach(property -> {
            if("lang".equals(property.getName())){
                properties.remove(property);
            }
        });
    }
    @Override
    protected void old(News model) {
        String lang = getRequest().getParameter("lang");
        LOG.info("控制器设置语言：" + lang);
        model.setLang(lang);
    }
    //方式二：使用IN语句
    @Override
    public PropertyCriteria buildPropertyCriteria(){
        String infoTypeIdStr = getRequest().getParameter("infoTypeId");
        int infoTypeId = Integer.parseInt(infoTypeIdStr);
        PropertyCriteria propertyCriteria=new PropertyCriteria();
        if(infoTypeId>0){
            InfoType obj=getService().retrieve(InfoType.class, infoTypeId);
            //获取orgId的所有子机构的ID
            List<Integer> infoTypeIds=InfoTypeService.getChildIds(obj);
            //加上orgId
            infoTypeIds.add(obj.getId());
            
            PropertyEditor pe=new PropertyEditor("infoType.id", Operator.in, PropertyType.List, infoTypeIds);
            propertyCriteria.addPropertyEditor(pe);
            
            return propertyCriteria;
        }
         //infoTypeId==-1或infoTypeId<0代表为根节点，不加过滤条件
        return null;
    }
    @Override
    protected void renderJsonForRetrieve(Map map) {
        String lang = getRequest().getParameter("lang");
        model.setLang(lang);
        render(map, model);
        map.put("infoTypeId", model.getInfoType().getId());
        map.put("content", model.getContent());
    }
    @Override
    protected void renderJsonForQuery(List result) {
        for (News news : page.getModels()) {
            //重新刷新，否则取不到News.newsContents，因为使用了延迟加载
            news=getService().retrieve(modelClass, news.getId());
            Map temp = new HashMap();
            render(temp,news);

            result.add(temp);
        }
    }
    @Override
    protected void render(Map map,News model){
        String lang = getRequest().getParameter("lang");
        model.setLang(lang);
        map.put("id", model.getId());
        map.put("version", model.getVersion());
        map.put("title", model.getTitle());
        map.put("username", model.getOwnerUser().getUsername());
        map.put("orgname", model.getOwnerUser().getOrg().getOrgName());
        model.getInfoType().setLang(lang);
        map.put("infoTypeName", model.getInfoType().getInfoTypeName());
        map.put("createTime", DateTypeConverter.toDefaultDateTime(model.getCreateTime()));
        map.put("updateTime", DateTypeConverter.toDefaultDateTime(model.getUpdateTime()));
        map.put("enabled", model.isEnabled()==true?"是":"否");
    }
}