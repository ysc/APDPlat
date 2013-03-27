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

package com.apdplat.platform.action;

/**
 *
 * 控制器接口，此控制器中定义的命令由客户代码直接调用
 * @author 杨尚川
 *
 */
public interface Action {

    /**
     * 添加一个特定的模型
     * @return
     */
    public String create();

    /**
     * 获取添加模型的表单
     * @return
     */
    public String createForm();

    /**
     * 检索一个特定的模型
     * @return
     */
    public String retrieve();

    /**
     * 更新一个特定的完整的模型
     * @return
     */
    public String updateWhole();

    /**
     * 更新一个特定模型的部分数据
     * @return
     */
    public String updatePart();

    /**
     * 获取更新模型的表单
     * @return
     */
    public String updateForm();

    /**
     * 删除一系列指定的模型
     * @return
     */
    public String delete();

    /**
     * 根据特定的条件查询符合条件的一系列模型，从数据库中查
     * @return
     */
    public String query();

    /**
     * 根据特定的条件搜索符合条件的一系列模型，从全文检索系统中搜索
     * @return
     */
    public String search();
}