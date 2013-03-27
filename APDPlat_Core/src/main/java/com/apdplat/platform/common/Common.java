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

package com.apdplat.platform.common;

import com.apdplat.platform.criteria.OrderCriteria;
import com.apdplat.platform.criteria.PageCriteria;
import com.apdplat.platform.criteria.Property;
import com.apdplat.platform.criteria.PropertyCriteria;
import com.apdplat.platform.model.Model;
import com.apdplat.platform.result.Page;
import java.util.List;

public interface Common<T extends Model> {
	/**
	 * CRUD操作中的C
	 * @param model
	 */
    public void create(T model);
    /**
     * CRUD操作中的R
     * @param modelId
     * @return
     */
    public T retrieve(Integer modelId);
    /**
     * CRUD操作中的U
     * @param model
     */
    public void update(T model);
    /**
     * CRUD操作中的D
     * @param modelId
     */
    public void delete(Integer modelId);


    /**
     * 更新部分属性
     * @param modelId
     * @param propertys
     */
    public void update(Integer modelId,List<Property> properties);
    /**
     * 查询第一页数据，默认最新添加的数据排在最前面
     * @return
     */
    public Page<T> query();
    /**
     * 分页查询数据，默认最新添加的数据排在最前面
     * @param pageCriteria 页面条件
     * @return
     */
    public Page<T> query(PageCriteria pageCriteria);
    /**
     *
     * @param pageCriteria 页面条件
     * @param filterCriteria 多个属性过滤条件
     * @param sortCriteria 多个排序条件
     * @return
     */
    public Page<T> query(PageCriteria pageCriteria,PropertyCriteria propertyCriteria);
    /**
     *
     * @param pageCriteria 页面条件
     * @param filterCriteria 多个属性过滤条件
     * @param sortCriteria 多个排序条件
     * @return
     */
    public Page<T> query(PageCriteria pageCriteria,PropertyCriteria propertyCriteria,OrderCriteria orderCriteria);
}