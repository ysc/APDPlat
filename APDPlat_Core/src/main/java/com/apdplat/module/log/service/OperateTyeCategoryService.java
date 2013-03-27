/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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