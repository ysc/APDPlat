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

package com.apdplat.module.monitor.service;

import com.apdplat.platform.service.SingleService;
import com.apdplat.platform.util.SortUtils;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jdom.Element;
import org.springframework.stereotype.Service;

/**
 *
 * @author ysc
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
        Map.Entry[] entrys=null;
        if(sort){
            //根据VALUE排序
            entrys=SortUtils.getSortedMapByValue(data);
            for(Map.Entry<String,Long> entry : entrys){
                Element element = createSet(entry.getKey(), entry.getValue());
                rootElement.addContent(element);
            }
        }else{
            //如果不根据VALUE排序，则根据KEY排序
//            Collection<String> keys=data.keySet();
//            List<String> list=new ArrayList<String>();
//            for(String key : keys){
//                list.add(key);
//            }
//            Collections.sort(list);
            for(String key : data.keySet()){
                Element element = createSet(key, data.get(key));
                rootElement.addContent(element);
            }
        }
    }
}