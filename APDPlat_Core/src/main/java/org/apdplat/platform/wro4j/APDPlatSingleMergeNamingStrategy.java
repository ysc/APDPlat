package org.apdplat.platform.wro4j;


import java.io.IOException;
import java.io.InputStream;
import ro.isdc.wro.model.resource.support.naming.NamingStrategy;

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

/**
 *SingleMerge是指对platform/include/common.jsp所引用的JS(CSS)合并为一个文件
 * @author 杨尚川
 */
public class APDPlatSingleMergeNamingStrategy implements NamingStrategy{
    @Override
    public String rename(String originalName, InputStream inputStream) throws IOException {
        System.out.println("originalName:"+originalName);
        if(originalName.contains("apdplat_merge")){
            originalName="platform/include/"+originalName;
        }
        if(originalName.contains("login_merge.js")){
            originalName="js/"+originalName;
        }
        if(originalName.contains("login_merge.css")){
            originalName="css/"+originalName;
        }
        
        System.out.println("originalName:"+originalName);
        return originalName;
    }
}
