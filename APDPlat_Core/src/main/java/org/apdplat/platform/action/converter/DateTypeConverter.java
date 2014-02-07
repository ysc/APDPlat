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

import org.apdplat.platform.log.APDPlatLogger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import org.apache.struts2.util.StrutsTypeConverter;
import org.apdplat.platform.log.APDPlatLoggerFactory;



/**
 *日期转换
 * @author 杨尚川
 */

public class DateTypeConverter extends StrutsTypeConverter {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(DateTypeConverter.class);

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    //暂时只考虑这几种日期格式
    public static final DateFormat[] ACCEPT_DATE_FORMATS = {
        new SimpleDateFormat(DEFAULT_DATE_FORMAT),
        new SimpleDateFormat("yyyy年MM月dd日"),
        new SimpleDateFormat("yyyy/MM/dd")};

    @Override
    public Object convertFromString(Map context, String[] values, Class toClass) {
        if (values[0] == null || values[0].trim().equals("")) {
            return null;
        }
        if (values[0].contains(":")) {
            try {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(values[0]);
            } catch (ParseException e) {
                LOG.error("转换失败",e);
            }
        }
        for (DateFormat format : ACCEPT_DATE_FORMATS) {
            try {
                return format.parse(values[0]);
            } catch (    ParseException | RuntimeException e) {
                continue;
            }
        }
        LOG.debug("can not format date string:" + values[0]);
        return null;
    }

    public static Date fromString(String date) {
        if (date == null) {
            return null;
        }
        Date r = null;
        try {
            r = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException ex) {
            LOG.debug(date + "转换成日期失败");
        }
        return r;
    }

    public static String toDefaultDate(Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public static String toSwitchContentDateTime(Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat("yyyyMMddHHmm").format(date);
    }
    public static String toSwitchContentDate(Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat("yyyyMMdd").format(date);
    }
    public static String toDefaultDateTime(Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
    public static Date fromDefaultDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (Exception ex) {
            LOG.debug(ex.getMessage());
        }
        return null;
    }
    public static Date fromDefaultDateTime(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
        } catch (Exception ex) {
            LOG.debug(ex.getMessage());
        }
        return null;
    }

    public static String toFileName(Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(date);
    }

    @Override
    public String convertToString(Map context, Object o) {
        if (o instanceof Date) {
            SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
            try {
                return format.format((Date) o);
            } catch (RuntimeException e) {
                return "";
            }
        }
        return "";
    }
}