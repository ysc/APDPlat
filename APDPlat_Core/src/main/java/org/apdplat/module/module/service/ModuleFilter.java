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

package org.apdplat.module.module.service;

import org.apdplat.module.module.model.Module;
import java.util.List;

/**
 *
 * @author 杨尚川
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