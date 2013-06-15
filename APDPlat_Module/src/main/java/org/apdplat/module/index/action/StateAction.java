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

package org.apdplat.module.index.action;

import org.apdplat.module.index.model.IndexDir;
import org.apdplat.module.index.service.IndexFileService;
import org.apdplat.platform.action.ExtJSActionSupport;
import org.apdplat.platform.action.converter.DateTypeConverter;
import org.apdplat.platform.util.Struts2Utils;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Scope("prototype")
@Controller
@Namespace("/index")
public class StateAction extends ExtJSActionSupport {

    private String dir;
    public String store(){
        List<IndexDir> dirs=IndexFileService.getIndexDirs();
        List<Map<String,String>> data=new ArrayList<>();
        for(IndexDir item : dirs){
            Map<String,String> map=new HashMap<>();
            map.put("value", item.getEnglishName());
            map.put("text", item.getChineseName());
            data.add(map);
        }
        Struts2Utils.renderJson(data);
        return null;
    }

    public String query() {
        if(StringUtils.isEmpty(dir)){
            return null;
        }
        int start=super.getStart();
        int len=super.getLimit();
        if(start==-1){
            start=0;
        }
        if(len==-1){
            len=10;
        }
       
        List<File> indexes=IndexFileService.getIndexFiles(dir);
        LOG.info("获取 "+dir+" 的索引文件");
        LOG.info("索引文件数量为： "+indexes.size());
        len=start+len;
        if(len>indexes.size()){
            len=indexes.size();
        }
        List<File> models=new ArrayList<>();
        for(int i=start;i<len;i++){
            models.add(indexes.get(i));
        }
        
        Map json = new HashMap();
        json.put("totalProperty", indexes.size());
        List<Map> result = new ArrayList<>();
        renderJsonForQuery(result,models);
        json.put("root", result);
        Struts2Utils.renderJson(json);
        return null;
    }
    protected void renderJsonForQuery(List result,List<File> indexes) {
        for (File index : indexes) {
            Map map = new HashMap();
            map.put("name", index.getName());
            map.put("lastModified", DateTypeConverter.toDefaultDateTime(new Date(index.lastModified())));
            float len=(float)index.length()/1024;
            map.put("length", len);
            
            result.add(map);
        }
    }

    public void setDir(String dir) {
        this.dir = dir;
    }
}