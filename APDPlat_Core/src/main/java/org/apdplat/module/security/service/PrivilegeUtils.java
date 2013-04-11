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

package org.apdplat.module.security.service;

/**
 *
 * @author 杨尚川
 */
public class PrivilegeUtils {
    public static String process(String str){
        str=str.replace("A", "-a");
        str=str.replace("B", "-b");
        str=str.replace("C", "-c");
        str=str.replace("D", "-d");
        str=str.replace("E", "-e");
        str=str.replace("F", "-f");
        str=str.replace("G", "-g");
        str=str.replace("H", "-h");
        str=str.replace("I", "-i");
        str=str.replace("J", "-j");
        str=str.replace("K", "-k");
        str=str.replace("L", "-l");
        str=str.replace("M", "-m");
        str=str.replace("N", "-n");
        str=str.replace("O", "-o");
        str=str.replace("P", "-p");
        str=str.replace("Q", "-q");
        str=str.replace("R", "-r");
        str=str.replace("S", "-s");
        str=str.replace("T", "-t");
        str=str.replace("U", "-u");
        str=str.replace("V", "-v");
        str=str.replace("W", "-w");
        str=str.replace("X", "-x");
        str=str.replace("Y", "-y");
        str=str.replace("Z", "-z");
        return str;
    }
}