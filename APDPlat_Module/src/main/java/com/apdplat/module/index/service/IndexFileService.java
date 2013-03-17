package com.apdplat.module.index.service;

import com.apdplat.module.index.model.IndexDir;
import com.apdplat.platform.model.ModelMetaData;
import com.apdplat.platform.search.IndexManager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *获取索引目录和索引文件
 * @author ysc
 */
public class IndexFileService {
    private static final  File file;
    
    static{
        //compass存放lucene索引的根目录
        file=new File(IndexManager.getIndexDir(),"index");
    }
    /**
     * 获取所有对象对应的索引目录
     * @return  索引目录名称列表
     */
    public static List<IndexDir> getIndexDirs() {
        List<IndexDir> dirs=new ArrayList<>();
        
        File[] files=file.listFiles();
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
        File dirFile=new File(file,dir);
        List<File> result=new ArrayList<>();
        
        File[] files=dirFile.listFiles();
        for(int i=0;i<files.length;i++){
            File f=files[i];
            result.add(f);
        }
        
        return result;
    }
    
}
