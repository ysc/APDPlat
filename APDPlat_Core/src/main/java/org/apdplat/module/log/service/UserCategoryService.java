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
public class UserCategoryService  extends CategoryService{
    
    public String getXML(List<OperateStatistics> data){
        //创建根元素
        Element rootElement = createRootElement("","");    
        //创建categories       
        Element categories=createCategories(data);        
        rootElement.addContent(categories);
        //创建datasets
        Element addDataset=createAddDataset(data);   
        rootElement.addContent(addDataset);
        Element deleteDataset=createDeleteDataset(data);   
        rootElement.addContent(deleteDataset);
        Element updateDataset=createUpdateDataset(data);   
        rootElement.addContent(updateDataset);
        //格式化输出，将对象转换为XML
        String xml=formatXML(rootElement);
        
        return xml;
    }

    private Element createCategories(List<OperateStatistics> data){
    	Element element = new Element("categories");
        data.forEach(item -> {
            Element subElement = createCategory(item.getUsername());
            element.addContent(subElement);
        });
        return element;
    }

    private Element createAddDataset(List<OperateStatistics> data) {
    	Element element = new Element("dataset");
    	element.setAttribute(new Attribute("seriesName", "添加数据"));
        data.forEach(item -> {
            Element subElement = createDataset(item.getAddCount());
            element.addContent(subElement);
        });
        return element;
    }

    private Element createDeleteDataset(List<OperateStatistics> data) {
    	Element element = new Element("dataset");
    	element.setAttribute(new Attribute("seriesName", "删除数据"));
        data.forEach(item -> {
            Element subElement = createDataset(item.getDeleteCount());
            element.addContent(subElement);
        });
        return element;
    }

    private Element createUpdateDataset(List<OperateStatistics> data) {
    	Element element = new Element("dataset");
    	element.setAttribute(new Attribute("seriesName", "修改数据"));
        data.forEach(item -> {
            Element subElement = createDataset(item.getUpdateCount());
            element.addContent(subElement);
        });
        return element;
    }
}