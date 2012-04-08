package com.apdplat.module.index.service;

import com.apdplat.module.index.model.IndexDir;
import com.apdplat.platform.model.ModelMetaData;
import com.apdplat.platform.search.IndexManager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ysc
 */
public class IndexFileService {
    private static  File file = null;
    
    static{
        file=IndexManager.getIndexDir();
        file=new File(file,"index");
    }
    
    public static List<IndexDir> getIndexDirs() {
        List<IndexDir> dirs=new ArrayList<IndexDir>();
        
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

    public static List<File> getIndexFiles(String dir) {
        File dirFile=new File(file,dir);
        List<File> result=new ArrayList<File>();
        
        File[] files=dirFile.listFiles();
        for(int i=0;i<files.length;i++){
            File f=files[i];
            result.add(f);
        }
        
        return result;
    }
    
}
