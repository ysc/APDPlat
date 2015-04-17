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

package org.apdplat.platform.action;

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