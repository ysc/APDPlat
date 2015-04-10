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

package org.apdplat.module.info.service;

import org.apdplat.module.info.model.InfoType;
import org.apdplat.module.info.model.InfoTypeContent;
import org.apdplat.platform.criteria.Criteria;
import org.apdplat.platform.criteria.Operator;
import org.apdplat.platform.criteria.PropertyCriteria;
import org.apdplat.platform.criteria.PropertyEditor;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.result.Page;
import org.apdplat.platform.service.ServiceFacade;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 * @author 杨尚川
 */
@Service
public class InfoTypeService {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(InfoTypeService.class);

    public static List<Integer> getChildIds(InfoType obj) {
        List<Integer> ids=new ArrayList<>();
        List<InfoType> child=obj.getChild();
        child.forEach(item -> {
            ids.add(item.getId());
            ids.addAll(getChildIds(item));
        });
        return ids;
    }
    @Resource(name="serviceFacade")
    private ServiceFacade serviceFacade;

    
    public String toRootJson(String lang){
        InfoType infoType=getRootInfoType();
        infoType.setLang(lang);
        
        if(infoType==null){
            LOG.error("获取根新闻类别失败！");
            return "";
        }
        StringBuilder json=new StringBuilder();
        json.append("[");

        json.append("{'text':'")
            .append(infoType.getInfoTypeName())
            .append("','id':'")
            .append(infoType.getId());
            if(infoType.getChild().isEmpty()){
                json.append("','leaf':true,'cls':'file'");
            }else{
                json.append("','leaf':false,'cls':'folder'");
            }
        json.append("}");
        json.append("]");
        
        return json.toString();
    }
    public String toJson(int infoTypeId, String lang){
        InfoType infoType=serviceFacade.retrieve(InfoType.class, infoTypeId);
        if(infoType==null){
            LOG.error("获取ID为 "+infoType+" 的新闻类别失败！");
            return "";
        }
        List<InfoType> child=infoType.getChild();
        if(child.isEmpty()){
            return "";
        }
        StringBuilder json=new StringBuilder();
        json.append("[");

        child.forEach(item -> {
            item.setLang(lang);
            json.append("{'text':'")
                .append(item.getInfoTypeName())
                .append("','id':'")
                .append(item.getId());
                if(item.getChild().isEmpty()){
                    json.append("','leaf':true,'cls':'file'");
                }else{
                    json.append("','leaf':false,'cls':'folder'");
                }
           json .append("},");
        });
        //删除最后一个,号，添加一个]号
        json.setLength(json.length()-1);
        json.append("]");

        return json.toString();
    }
    public InfoType getRootInfoType(){
        try{
            PropertyCriteria propertyCriteria = new PropertyCriteria(Criteria.or);
            propertyCriteria.addPropertyEditor(new PropertyEditor("infoTypeName", Operator.eq, "String","新闻类别"));
            Page<InfoTypeContent> page = serviceFacade.query(InfoTypeContent.class, null, propertyCriteria);
            if (page.getTotalRecords() == 1) {
                return page.getModels().get(0).getInfoType();
            }
        }catch(Exception e){
            LOG.error("获取ROOT失败",e);
        }
        return null;
    }
}