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

package org.apdplat.module.dictionary.service;

import org.apdplat.module.dictionary.model.Dic;
import org.apdplat.module.dictionary.model.DicItem;
import org.apdplat.platform.criteria.Operator;
import org.apdplat.platform.criteria.PropertyCriteria;
import org.apdplat.platform.criteria.PropertyEditor;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.service.ServiceFacade;
import java.util.List;
import javax.annotation.Resource;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DicService {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(DicService.class);
    @Resource(name="serviceFacade")
    private ServiceFacade serviceFacade;
    
    public Dic getRootDic(){
        PropertyEditor propertyEditor=new PropertyEditor("english",Operator.eq,"root");

        PropertyCriteria propertyCriteria=new PropertyCriteria();
        propertyCriteria.addPropertyEditor(propertyEditor);

        List<Dic> dics = serviceFacade.query(Dic.class, null, propertyCriteria).getModels();
        if(dics!=null && dics.size()==1){
            return dics.get(0);
        }
        LOG.error("有多个根词典!");
        return null;
    }

    public Dic getDic(String english){
        PropertyEditor propertyEditor=new PropertyEditor("english",Operator.eq,english);

        PropertyCriteria propertyCriteria=new PropertyCriteria();
        propertyCriteria.addPropertyEditor(propertyEditor);

        List<Dic> page=serviceFacade.query(Dic.class, null, propertyCriteria).getModels();
        if(page.isEmpty()){
            return null;
        }
        return page.get(0);
    }

    public Dic getDic(int id){
        PropertyEditor propertyEditor=new PropertyEditor("id",Operator.eq,Integer.toString(id));

        PropertyCriteria propertyCriteria=new PropertyCriteria();
        propertyCriteria.addPropertyEditor(propertyEditor);

        List<Dic> page=serviceFacade.query(Dic.class, null, propertyCriteria).getModels();
        if(page.isEmpty()){
            LOG.error("没有找到ID等于"+id+"的字典");
            return null;
        }
        return page.get(0);
    }
    
    public String toStoreJson(Dic dic){
        StringBuilder json=new StringBuilder();
        List<Dic> subDics=dic.getSubDics();
        
        if(subDics.size()>0){
            json.append("[");
            for(Dic d : subDics){
                json.append("{'text':'")
                    .append(d.getChinese())
                    .append("','id':'")
                    .append(d.getId())
                    .append("','iconCls':'")
                    .append(d.getEnglish())
                    .append("'")
                    .append(",children:")
                    .append(toStoreJson(d))
                    .append(",'leaf':false");
                
                json.append("},");
            }
            json=json.deleteCharAt(json.length()-1);
            json.append("]");
        }else{
            List<DicItem> dicItems=dic.getDicItems();
            if(dicItems.size()>0){
                json.append("[");
                for(DicItem d : dicItems){
                    json.append("{'text':'")
                        .append(d.getName())
                        .append("','id':'")
                        .append(d.getId())
                        .append("','iconCls':'")
                        .append(d.getName())
                        .append("','leaf':true")
                        .append("},");
                }
                json=json.deleteCharAt(json.length()-1);
                json.append("]");
            }
        }
        
        return json.toString();
    }
    
    public String toJson(Dic dic){
        StringBuilder json=new StringBuilder();
        List<Dic> subDics=dic.getSubDics();
        
        if(subDics.size()>0){
            json.append("[");
            for(Dic d : subDics){
                json.append("{'text':'")
                    .append(d.getChinese())
                    .append("','id':'")
                    .append(d.getId())
                    .append("','iconCls':'")
                    .append(d.getEnglish())
                    .append("'");
                if(d.getSubDics().size()>0){
                    json.append(",children:")
                        .append(toJson(d))
                        .append(",'leaf':false");
                }else{
                    json.append(",'leaf':true");
                }
                json.append("},");
            }
            json=json.deleteCharAt(json.length()-1);
            json.append("]");
        }
        
        return json.toString();
    }
}