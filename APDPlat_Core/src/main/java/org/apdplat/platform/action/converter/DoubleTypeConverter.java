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

package org.apdplat.platform.action.converter;

/**
 * 日期转换
 * @author 杨尚川
 */

import org.apdplat.platform.log.APDPlatLogger;
import java.util.Map;
import org.apdplat.platform.log.APDPlatLoggerFactory;

public class DoubleTypeConverter implements TypeConverter{
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(DoubleTypeConverter.class);


    @Override
    public Object convertFromString(Map context, String[] values, Class toClass) {
        if (values[0] == null || values[0].trim().equals("")) {
            return 0;
        }
        try{
            return Double.parseDouble(values[0].trim());
        }catch(Exception e){
            LOG.info("字符串:"+values[0].trim()+"转换为数字失败");
        }
        return 0;
    }
    @Override
    public String convertToString(Map context, Object o) {
        if (o == null)
            return "0";
        return o.toString();
    }
}