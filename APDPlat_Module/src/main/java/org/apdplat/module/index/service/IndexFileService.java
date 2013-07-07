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
import org.apdplat.platform.model.ModelMetaData;
import org.apdplat.platform.search.IndexManager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *获取索引目录和索引文件
 * @author 杨尚川
 */
public class IndexFileService {
    private static final  File FILE;
    
    static{
        //compass存放lucene索引的根目录
        FILE=new File(IndexManager.getIndexDir(),"index");
    }
    /**
     * 获取所有对象对应的索引目录
     * @return  索引目录名称列表
     */
    public static List<IndexDir> getIndexDirs() {
        List<IndexDir> dirs=new ArrayList<>();
        
        File[] files=FILE.listFiles();
        for(int i=0;i<files.length;i++){
            File f=files[i];
            IndexDir dir=new IndexDir();
            dir.setEnglishName(f.getName());
            dir.setChineseName(ModelMetaData.getMetaData(f.getName()));
            dirs.add(dir);
        }
        
        return dirs;
    }
    /**
     * 获取某个对象的所有索引文件
     * @param dir 索引对象的目录名称
     * @return 索引文件列表
     */
    public static List<File> getIndexFiles(String dir) {
        File dirFile=new File(FILE,dir);
        List<File> result=new ArrayList<>();
        
        File[] files=dirFile.listFiles();
        for(int i=0;i<files.length;i++){
            File f=files[i];
            result.add(f);
        }
        
        return result;
    }
    
}