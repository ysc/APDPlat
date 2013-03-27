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

package com.apdplat.module.module.service;

import com.apdplat.module.module.model.Module;
import java.util.List;

/**
 *
 * @author ysc
 */
public interface ModuleFilter {
    public void filter(List<Module> subModules);
    /**
     * 生成的JSON是否包含JS脚本，此脚本用于打开模块对应的页面
     * @return 
     */
    public boolean script();
     /**
     * 是否生成一颗完整的树形功能菜单
     * @return 
     */
    public boolean recursion();
    /**
     * 生成的功能菜单树中是否包含命令
     * @return 
     */
    public boolean command();
}