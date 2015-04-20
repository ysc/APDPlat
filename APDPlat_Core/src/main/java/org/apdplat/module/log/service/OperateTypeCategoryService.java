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

package org.apdplat.module.log.service;

import org.apdplat.module.log.model.OperateStatistics;
import org.apdplat.platform.service.CategoryService;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.Element;
import org.springframework.stereotype.Service;

/**
 *
 * @author 杨尚川
 */
@Service
public class OperateTypeCategoryService extends CategoryService{
    
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
        data.forEach(item -> {
            Element dataset = createUserDataset(item);
            rootElement.addContent(dataset);
        });
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