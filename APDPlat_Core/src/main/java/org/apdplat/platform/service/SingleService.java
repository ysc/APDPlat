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

package org.apdplat.platform.service;

import java.util.LinkedHashMap;
import org.jdom.Attribute;
import org.jdom.Element;

/**
 *
 * @author 杨尚川
 */
public abstract class SingleService extends ChartService{
    
    public String getXML(LinkedHashMap<String,Long> data){
        //创建根元素
        Element rootElement = createRootElement("","");    
        
        //创建sets
        createSets(data,rootElement);   
        
        //格式化输出，将对象转换为XML
        String xml=formatXML(rootElement);
        
        return xml;
    }

    private void createSets(LinkedHashMap<String, Long> data, Element rootElement) {
        data.keySet().forEach(key -> {
            Element element = createSet(key, data.get(key));
            rootElement.addContent(element);
        });
    }
    
    protected static Element createSet(Object label,Long value) {
    	Element element = new Element("set");
    	element.setAttribute(new Attribute("label", label.toString()));
    	element.setAttribute(new Attribute("value", value.toString()));
        return element;
    }
}