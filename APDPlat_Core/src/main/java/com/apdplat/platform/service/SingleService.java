package com.apdplat.platform.service;

import java.util.LinkedHashMap;
import org.jdom.Attribute;
import org.jdom.Element;

/**
 *
 * @author ysc
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
        for(String key : data.keySet()){
            Element element = createSet(key, data.get(key));
            rootElement.addContent(element);
        }
    }
    
    protected static Element createSet(Object label,Long value) {
    	Element element = new Element("set");
    	element.setAttribute(new Attribute("label", label.toString()));
    	element.setAttribute(new Attribute("value", value.toString()));
        return element;
    }
}
