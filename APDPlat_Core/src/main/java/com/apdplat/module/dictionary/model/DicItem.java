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

package com.apdplat.module.dictionary.model;

import com.apdplat.platform.annotation.ModelAttr;
import com.apdplat.platform.annotation.ModelAttrRef;
import com.apdplat.platform.generator.ActionGenerator;
import com.apdplat.platform.model.Model;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableComponent;
import org.compass.annotations.SearchableProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
/**
 *数据字典项
 * @author 杨尚川
 */
@Entity
@Scope("prototype")
@Component
@XmlType(name = "DicItem")
@Searchable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE) 
public class DicItem extends Model {

    @ManyToOne
    @SearchableComponent
    @ModelAttr("数据字典")
    @ModelAttrRef("chinese")
    protected Dic dic;
    @ModelAttr("编码")
    protected String code;
    @SearchableProperty
    @ModelAttr("名称")
    protected String name;
    @ModelAttr("排序号")
    protected int orderNum;

    @XmlAttribute
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute
    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    @XmlTransient
    public Dic getDic() {
        return dic;
    }

    public void setDic(Dic dic) {
        this.dic = dic;
    }
    @Override
    public String getMetaData() {
        return "数据字典项";
    }
    public static void main(String[] args){
        DicItem obj=new DicItem();
        //生成Action
        ActionGenerator.generate(obj.getClass());
    }
}