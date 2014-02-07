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

import java.util.HashMap;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.log.APDPlatLoggerFactory;

/**
 *
 * @author 杨尚川
 */
public class ModuleCache {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(ModuleCache.class);
    private static final HashMap<String,String> cache=new HashMap<>();
    private ModuleCache(){}
    
    public static void put(String key, String value){
        cache.put(key, value);
    }
    public static String get(String key){
        return cache.get(key);
    }    
    public static void clear(){
        cache.clear();
        LOG.info("清空缓存");
    }
}
