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

package org.apdplat.module.monitor.service;

import org.apdplat.platform.service.SingleService;
import org.apdplat.platform.util.SortUtils;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jdom.Element;
import org.springframework.stereotype.Service;

/**
 *
 * @author 杨尚川
 */
@Service
public class ProcessTimeSingleService  extends SingleService{
    
    public String getXML(LinkedHashMap<String,Long> data, boolean sort){
        //创建根元素
        Element rootElement = createRootElement("","");    
        
        //创建sets
        createSets(data,rootElement,sort);   
        
        //格式化输出，将对象转换为XML
        String xml=formatXML(rootElement);
        
        return xml;
    }
    /**
     * 
     * @param data
     * @param rootElement
     * @param sort 根据VALUE排序
     */
    private void createSets(LinkedHashMap<String, Long> data, Element rootElement, boolean sort) {
        if(sort){
            //根据VALUE排序
            data
            .entrySet()
            .stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .forEach(entry -> {
                Element element = createSet(entry.getKey(), entry.getValue());
                rootElement.addContent(element);
            });
        }else{
            data.keySet().forEach(key -> {
                Element element = createSet(key, data.get(key));
                rootElement.addContent(element);
            });
        }
    }
}