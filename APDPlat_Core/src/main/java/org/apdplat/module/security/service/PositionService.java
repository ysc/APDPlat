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

package org.apdplat.module.security.service;

import org.apdplat.module.security.model.Position;
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
public class PositionService {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(PositionService.class);
    @Resource(name="serviceFacade")
    private ServiceFacade serviceFacade;

    public static List<String> getChildNames(Position position){
        List<String> names=new ArrayList<>();
        List<Position> child=position.getChild();
        for(Position item : child){
            names.add(item.getPositionName());
            names.addAll(getChildNames(item));
        }
        return names;
    }
    public static List<Integer> getChildIds(Position position){
        List<Integer> ids=new ArrayList<>();
        List<Position> child=position.getChild();
        for(Position item : child){
            ids.add(item.getId());
            ids.addAll(getChildIds(item));
        }
        return ids;
    }
    public static boolean isParentOf(Position parent,Position child){
        Position position=child.getParent();
        while(position!=null){
            if(position.getId()==parent.getId()){
                return true;
            }
            position=position.getParent();
        }
        return false;
    }
    
    public String toRootJson(boolean recursion){
        Position rootPosition=getRootPosition();
        if(rootPosition==null){
            LOG.error("获取根岗位失败！");
            return "";
        }
        StringBuilder json=new StringBuilder();
        json.append("[");

        json.append("{'text':'")
            .append(rootPosition.getPositionName())
            .append("','id':'position-")
            .append(rootPosition.getId());
            if(rootPosition.getChild().isEmpty()){
                json.append("','leaf':true,'cls':'file'");
            }else{
                json.append("','leaf':false,'cls':'folder'");
                
                if (recursion) {
                    for(Position item : rootPosition.getChild()){
                        json.append(",children:").append(toJson(item.getId(), recursion));
                    }
                }
            }
        json.append("}");
        json.append("]");
        
        return json.toString();
    }
    public String toJson(int positionId, boolean recursion){
        Position position=serviceFacade.retrieve(Position.class, positionId);
        if(position==null){
            LOG.error("获取ID为 "+positionId+" 的岗位失败！");
            return "";
        }
        List<Position> child=position.getChild();
        if(child.isEmpty()){
            return "";
        }
        StringBuilder json=new StringBuilder();
        json.append("[");

        
        for(Position item : child){
            json.append("{'text':'")
                .append(item.getPositionName())
                .append("','id':'position-")
                .append(item.getId());
                if(item.getChild().isEmpty()){
                    json.append("','leaf':true,'cls':'file'");
                }else{
                    json.append("','leaf':false,'cls':'folder'");
                    if (recursion) {
                        json.append(",children:").append(toJson(item.getId(), recursion));
                    }
                }
           json .append("},");
        }
        //删除最后一个,号，添加一个]号
        json=json.deleteCharAt(json.length()-1);
        json.append("]");

        return json.toString();
    }
    public Position getRootPosition(){
        PropertyCriteria propertyCriteria = new PropertyCriteria(Criteria.or);
        propertyCriteria.addPropertyEditor(new PropertyEditor("positionName", Operator.eq, "String","岗位"));
        Page<Position> page = serviceFacade.query(Position.class, null, propertyCriteria);
        if (page.getTotalRecords() == 1) {
            return page.getModels().get(0);
        }
        return null;
    }
}