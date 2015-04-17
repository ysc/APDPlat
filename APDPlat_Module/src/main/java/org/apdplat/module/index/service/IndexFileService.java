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

package org.apdplat.module.index.service;

import org.apdplat.module.index.model.IndexDir;
import java.util.ArrayList;
import java.util.List;

/**
 *获取索引目录和索引文件
 * @author 杨尚川
 */
public class IndexFileService {
    /**
     * 获取所有对象对应的索引目录
     * @return  索引目录名称列表
     */
    public static List<IndexDir> getIndexDirs() {
        List<IndexDir> dirs=new ArrayList<>();

        return dirs;
    }
}