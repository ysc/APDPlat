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

package org.apdplat.platform.util;

import java.text.*;

/**
 * 文件大小格式化类。
 * 这个类本来应该是从NumberFormat类继承而来，但是由于NumberFormat的format(long number)被定义为
 * final的，因此采取从Format继承，其内容实际就是一个NumberFormat。
 * 
 */
public class FileSizeFormat extends Format {
  public static final int BYTE = 0;
  public static final int KILO = 1;
  public static final int MEGA = 2;
  public static final int GIGA = 3;
  public static final long KILOBYTE = 1024;
  public static final long MEGABYTE = 1024 * 1024;
  public static final long GIGABYTE = 1024 * 1024 * 1024;
  public static final String[] defaultUnitNames = {
      "B", "K", "M", "G"};
  public static final String defaultFormat = "#,##0.##";
  String format;
  String[] unitNames;
  boolean showUnitName = false;
  NumberFormat formatter;
  /**
   * 得到一个缺省的FileSizeFormat。
   * 即不显示尺寸的单位，格式为缺省格式"#,##0.##"。
   * 
   */
  public FileSizeFormat() {
    this(defaultFormat, false, null);
  }

  /**
   * 根据指定的格式构造一个FileSizeFormat。
   * 不显示尺寸的单位。
   * @param format 格式
   * 
   */
  public FileSizeFormat(String format) {
    this(format, false, null);
  }

  /**
   * 根据指定的属性构造一个FileSizeFormat。
   * @param showUnitName 是否显示单位
   * 
   */
  public FileSizeFormat(boolean showUnitName) {
    this(defaultFormat, showUnitName, defaultUnitNames);
  }

  /**
   * 根据指定单位构造一个FileSizeFormat。
   * 显示尺寸的单位。
   * @param unitNames 单位，依次为字节、千字节、兆字节和百兆字节的单位。
   * 
   */
  public FileSizeFormat(String[] unitNames) {
    this(defaultFormat, true, unitNames);
  }

  /**
   * 根据指定的格式和单位构造一个FileSizeFormat。
   * 显示尺寸的单位。
   * @param format 格式
   * @param unitNames 单位，依次为字节、千字节、兆字节和百兆字节的单位。
   * 
   */
  public FileSizeFormat(String format, String[] unitNames) {
    this(format, true, unitNames);
  }

  /**
   * 根据指定的格式和是否显示尺寸单位构造一个FileSizeFormat。
   * @param format 格式
   * @param showUnitName true的时候显示单位，使用缺省的单位。
   * 
   */
  public FileSizeFormat(String format, boolean showUnitName) {
    this(format, showUnitName, defaultUnitNames);
  }

  /**
   * 根据指定的格式和是否显示尺寸单位构造一个FileSizeFormat。
   * @param format 格式
   * @param showUnitName true的时候显示单位，使用缺省的单位。
   * @param unitNames 单位，依次为字节、千字节、兆字节和百兆字节的单位。
   * 
   */
  public FileSizeFormat(String format, boolean showUnitName, String[] unitNames) {
    this.format = format;
    formatter = new DecimalFormat(format);
    this.showUnitName = showUnitName;
    this.unitNames = unitNames;
  }

  /**
   * 得到可读的数字大小，一般用于文件尺寸。
   * @param number 数字，一般应该是Long类型的
   * @return 格式化以后的字符串
   * 
   */
  public String format(Number number) {
    return format(number.longValue(), getUnit(number.longValue()));
  }

  /**
   * 得到可读的数字大小。
   * @param size 原始大小
   * @return 格式化以后的字符串
   * 
   */
  public String format(long size) {
    return format(size, getUnit(size));
  }

  /**
   * 得到数字的单位，一般用于文件尺寸。
   * @param number 数字，一般应该是Long类型的
   * @return number的long值在1024以下时返回BYTE，依次类推直到GIGA。
   * 
   */
  public int getUnit(Number number) {
    return getUnit(number.longValue());
  }

  /**
   * 得到数字的单位。
   * @param size 原始大小
   * @return size的值在1024以下时返回BYTE，依次类推直到GIGA。
   * 
   */
  public int getUnit(long size) {
    if (size < KILOBYTE) {
      return BYTE;
    }
    else if (size < MEGABYTE) {
      return KILO;
    }
    else if (size < GIGABYTE) {
      return MEGA;
    }
    else {
      return GIGA;
    }
  }

  /**
   * 得到格式化的大小。
   * @param number 原始大小
   * @param unit 单位
   * @return 根据单位进行格式化后的字符串大小
   * 
   */
  public String format(Number number, int unit) {
    return format(number.longValue(), unit);
  }

  /**
   * 设置格式化时的格式。
   * @param format 格式
   * 
   */
  public void setFormat(String format) {
    this.format = format;

  }

  /**
   * 得到格式时的格式。
   * @return 格式时的格式
   * 
   */
  public String getFormat() {
    return format;
  }

  /**
   * 得到格式化的大小。
   * @param size 原始大小
   * @param unit 单位
   * @return 根据单位进行格式化后的字符串大小
   * 
   */
  public String format(long size, int unit) {
    String result;
    switch (unit) {
      case BYTE:
        result = formatter.format(size);
        break;
      case KILO:
        result = formatter.format( ( (double) size) / ( (double) KILOBYTE));
        break;
      case MEGA:
        result = formatter.format( ( (double) size) / ( (double) MEGABYTE));
        break;
      case GIGA:
        result = formatter.format( ( (double) size) / ( (double) GIGABYTE));
        break;
      default:
        result = formatter.format(size);
        break;
    }
    if (showUnitName == true) {
      result += unitNames[unit];
    }
    return result;
  }

  /**
   * 设置显示单位的名称。
   * 这个方法同时也会将设置显示单位名称。
   * @param unitNames 名称数组
   * 
   */
  public void setUnitNames(String[] unitNames) {
    showUnitName = true;
    this.unitNames = unitNames;
  }

  /**
   * 返回显示名称数组。
   * @return 名称数组
   * 
   */
  public String[] getUnitNames() {
    return unitNames;
  }

  /**
   * 设置是否显示单位名称。
   * 如果显示单位名称但是名称数组为null时设置为缺省单位名称。
   * @param visible 是否显示单位名称
   * 
   */
  public void setUnitNameVisible(boolean visible) {
    showUnitName = visible;
    if (showUnitName == true && unitNames == null) {
      unitNames = defaultUnitNames;
    }
  }

  /**
   * 是否显示单位名称。
   * @return 显示时返回true，否则返回false
   * 
   */
  public boolean isUnitNameVisible() {
    return showUnitName;
  }

  /**
   * 解析字符串为一个对象。
   * @param source 要解析的对象字符串
   * @param pos 解析位置
   * @return 经NumberFormat解析的结果
   * 
   */
    @Override
  public Object parseObject(String source,
                            ParsePosition pos) {
    return formatter.parseObject(source, pos);
  }

  /**
   * 根据指定的对象和附加对象进行格式化。
   * @param obj 格式化的对象
   * @param toAppendTo 附件的文本信息
   * @param pos 格式化的文本中的位置信息
   * @return 经NumberFormat格式化以后的StringBuffer
   * 
   */
    @Override
  public StringBuffer format(Object obj,
                             StringBuffer toAppendTo,
                             FieldPosition pos) {
    return formatter.format(obj, toAppendTo, pos);

  }

}