package com.apdplat.module.log.service;

import com.apdplat.module.log.model.OperateStatistics;
import com.apdplat.platform.service.CategoryService;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.Element;
import org.springframework.stereotype.Service;

/**
 *
 * @author ysc
 */
@Service
public class OperateTyeCategoryService extends CategoryService{
    
    public String getXML(List<OperateStatistics> data){
        //创建根元素
        Element rootElement = createRootElement("","");     
        //创建categories
        Element categories=createCategories();        
        rootElement.addContent(categories);
        //创建datasets
        createUserDatasets(data,rootElement);         
        //格式化输出，将对象转换为XML
        String xml=formatXML(rootElement);
        
        return xml;
    }
    private Element createCategories(){
    	Element element = new Element("categories");
    	Element subElement = createCategory("添加数据");
        element.addContent(subElement);
        subElement = createCategory("删除数据");
        element.addContent(subElement);
        subElement = createCategory("修改数据");
        element.addContent(subElement);
        return element;
    }

    private void createUserDatasets(List<OperateStatistics> data, Element rootElement) {
    	for(OperateStatistics item : data){
            Element dataset = createUserDataset(item);
            rootElement.addContent(dataset);
        }
    }

    private Element createUserDataset(OperateStatistics data) {
    	Element element = new Element("dataset");
    	element.setAttribute(new Attribute("seriesName", data.getUsername()));
    	Element subElement = createDataset(data.getAddCount());
        element.addContent(subElement);
        subElement = createDataset(data.getDeleteCount());
        element.addContent(subElement);
        subElement = createDataset(data.getUpdateCount());
        element.addContent(subElement);
        return element;
    }
}
