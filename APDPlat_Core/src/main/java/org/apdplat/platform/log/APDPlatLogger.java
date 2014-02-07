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

package org.apdplat.platform.log;

import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * 日志输出支持多国语言切换解决方案接口
 * @author 杨尚川
 */
public interface APDPlatLogger extends Logger {
    
    public void setLocale(Locale locale);
    public Locale getLocale();
    
    public void trace(String msg, Locale locale);
    public void trace(String format, Object arg, Locale locale);
    public void trace(String format, Object arg1, Object arg2, Locale locale);
    public void trace(String format, Object[] argArray, Locale locale);
    public void trace(String msg, Throwable t, Locale locale);

    public void trace(Marker marker, String msg, Locale locale);
    public void trace(Marker marker, String format, Object arg, Locale locale);
    public void trace(Marker marker, String format, Object arg1, Object arg2, Locale locale);
    public void trace(Marker marker, String format, Object[] argArray, Locale locale);
    public void trace(Marker marker, String msg, Throwable t, Locale locale);

    public void debug(String msg, Locale locale);
    public void debug(String format, Object arg, Locale locale);
    public void debug(String format, Object arg1, Object arg2, Locale locale);
    public void debug(String format, Object[] argArray, Locale locale);
    public void debug(String msg, Throwable t, Locale locale);

    public void debug(Marker marker, String msg, Locale locale);
    public void debug(Marker marker, String format, Object arg, Locale locale);
    public void debug(Marker marker, String format, Object arg1, Object arg2, Locale locale);
    public void debug(Marker marker, String format, Object[] argArray, Locale locale);
    public void debug(Marker marker, String msg, Throwable t, Locale locale);  

    public void info(String msg, Locale locale);
    public void info(String format, Object arg, Locale locale);
    public void info(String format, Object arg1, Object arg2, Locale locale);
    public void info(String format, Object[] argArray, Locale locale);
    public void info(String msg, Throwable t, Locale locale);

    public void info(Marker marker, String msg, Locale locale);
    public void info(Marker marker, String format, Object arg, Locale locale);
    public void info(Marker marker, String format, Object arg1, Object arg2, Locale locale);
    public void info(Marker marker, String format, Object[] argArray, Locale locale);
    public void info(Marker marker, String msg, Throwable t, Locale locale);

    public void warn(String msg, Locale locale);
    public void warn(String format, Object arg, Locale locale);
    public void warn(String format, Object[] argArray, Locale locale);
    public void warn(String format, Object arg1, Object arg2, Locale locale);
    public void warn(String msg, Throwable t, Locale locale);

    public void warn(Marker marker, String msg, Locale locale); 
    public void warn(Marker marker, String format, Object arg, Locale locale);
    public void warn(Marker marker, String format, Object arg1, Object arg2, Locale locale);  
    public void warn(Marker marker, String format, Object[] argArray, Locale locale);
    public void warn(Marker marker, String msg, Throwable t, Locale locale); 

    public void error(String msg, Locale locale);
    public void error(String format, Object arg, Locale locale);
    public void error(String format, Object arg1, Object arg2, Locale locale);
    public void error(String format, Object[] argArray, Locale locale);
    public void error(String msg, Throwable t, Locale locale);

    public void error(Marker marker, String msg, Locale locale); 
    public void error(Marker marker, String format, Object arg, Locale locale);
    public void error(Marker marker, String format, Object arg1, Object arg2, Locale locale);  
    public void error(Marker marker, String format, Object[] argArray, Locale locale);
    public void error(Marker marker, String msg, Throwable t, Locale locale);    
}
