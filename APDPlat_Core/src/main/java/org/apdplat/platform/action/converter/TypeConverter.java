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

import java.util.Map;

/**
 * 类型转换
 * @author 杨尚川
 */
public interface TypeConverter {
    /**
     * Converts one or more String values to the specified class.
     *
     * @param context the action context
     * @param values  the String values to be converted, such as those submitted from an HTML form
     * @param toClass the class to convert to
     * @return the converted object
     */
    public Object convertFromString(Map context, String[] values, Class toClass);

    /**
     * Converts the specified object to a String.
     *
     * @param context the action context
     * @param o       the object to be converted
     * @return the converted String
     */
    public String convertToString(Map context, Object o);
}
