package org.apdplat.platform.wro4j;


import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
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
 *
 * @author 杨尚川
 */
public class APDPlatMultiMergeNamingStrategy implements NamingStrategy{
    private static final Map<String,String> map = new HashMap<>();
    static{
        map.put("extjs_css_merge", "extjs/css/");
        map.put("extjs_js_merge", "extjs/js/");
        map.put("extjs_ux_merge", "extjs/ux/");
        map.put("extjs_ux_css_merge", "extjs/ux/css/");
        map.put("FusionCharts_merge", "FusionCharts/");
        map.put("ckeditor_merge", "ckeditor/");
        map.put("ckfinder_merge", "ckfinder/");
        map.put("DateTime_merge", "DateTime/");
        map.put("js_merge", "js/");
        map.put("platform_css_merge", "platform/css/");
        map.put("platform_js_merge", "platform/js/");
    }
    @Override
    public String rename(String originalName, InputStream inputStream) throws IOException {
        String originalNamePrefix=originalName.replace(".css", "").replace(".js", "");
        String path=map.get(originalNamePrefix);
        System.out.println("originalName:"+originalName);
        System.out.println("path:"+path);
        originalName=path+originalName;
        System.out.println("originalName:"+originalName);
        return originalName;
    }
}
