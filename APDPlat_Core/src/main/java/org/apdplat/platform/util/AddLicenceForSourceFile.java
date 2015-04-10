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

import java.io.*;
import java.util.ArrayList;
import java.util.List;
/**
 * 给JAVA源代码文件统一地添加licence信息头
 * 检查文件package、import、类级别注释、是否有public class
 * 用到了Java7的新特性，强大
 * @author ysc
 */
public class AddLicenceForSourceFile {
    private static int count = 0;
    private static List<String> fail = new ArrayList<>();
    private static List<String> wrong = new ArrayList<>();
    private static final String APDPLATPATH="D:\\Workspaces\\NetBeansProjects\\APDPlat";
    
    private static final List<String> jsExcludes=new ArrayList<>();
    
    static{
        //dir
        jsExcludes.add("DateTime");
        jsExcludes.add("FusionCharts");
        jsExcludes.add("ckeditor");
        jsExcludes.add("ckfinder");
        jsExcludes.add("extjs");
        //file
        jsExcludes.add("MSIE.PNG.js");
        jsExcludes.add("md5.js");
        jsExcludes.add("ripemd160.js");
        jsExcludes.add("sha1.js");
        jsExcludes.add("sha256.js");
        jsExcludes.add("sha512.js");
        jsExcludes.add("IconCombo.js");
        jsExcludes.add("swfupload.js");
    }
    
    private static final String JAVALICENSE="/**\n" +
" * \n" +
" * APDPlat - Application Product Development Platform\n" +
" * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com\n" +
" * \n" +
" * This program is free software: you can redistribute it and/or modify\n" +
" * it under the terms of the GNU General Public License as published by\n" +
" * the Free Software Foundation, either version 3 of the License, or\n" +
" * (at your option) any later version.\n" +
" * \n" +
" * This program is distributed in the hope that it will be useful,\n" +
" * but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
" * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n" +
" * GNU General Public License for more details.\n" +
" * \n" +
" * You should have received a copy of the GNU General Public License\n" +
" * along with this program.  If not, see <http://www.gnu.org/licenses/>.\n" +
" * \n" +
" */";
    private static final String JSPLICENSE="<%--\n" +
"   APDPlat - Application Product Development Platform\n" +
"   Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com\n" +
"   \n" +
"   This program is free software: you can redistribute it and/or modify\n" +
"   it under the terms of the GNU General Public License as published by\n" +
"   the Free Software Foundation, either version 3 of the License, or\n" +
"   (at your option) any later version.\n" +
"   \n" +
"   This program is distributed in the hope that it will be useful,\n" +
"   but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
"   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n" +
"   GNU General Public License for more details.\n" +
"   \n" +
"   You should have received a copy of the GNU General Public License\n" +
"   along with this program.  If not, see <http://www.gnu.org/licenses/>.\n" +
"--%>";
    public static void main(String[] args) {
        processJavaFile();
        processJspFile();
        processJsFile();
    }
    public static void processJavaFile(){
        //重置计算器
        count = 0;
        fail = new ArrayList<>();
        wrong = new ArrayList<>();
        
        addLicenceForJavaFile(new File(APDPLATPATH),JAVALICENSE);
        System.out.println("为 "+count+" 个Java源代码文件添加licence信息头");
        if(fail.size()>0){
            System.out.println("处理失败个数 "+fail.size());
            fail.forEach(f -> {
                System.out.println("        "+f);
            });
            throw new RuntimeException("失败：为Java源代码文件添加licence信息头");
        }
        if(wrong.size()>0){
            System.out.println("JAVA源代码错误个数 "+wrong.size());
            wrong.forEach(w -> {
                System.out.println("        "+w);
            });
            throw new RuntimeException("错误：为Java源代码文件添加licence信息头");
        }        
    }
    public static void processJspFile(){
        //重置计算器
        count = 0;
        fail = new ArrayList<>();
        wrong = new ArrayList<>();
        
        addLicenceForJspFile(new File(APDPLATPATH),JSPLICENSE);
        System.out.println("为 "+count+" 个Jsp源代码文件添加licence信息头");
        if(fail.size()>0){
            System.out.println("处理失败个数 "+fail.size());
            fail.forEach(f -> {
                System.out.println("        "+f);
            });
            throw new RuntimeException("失败：为Jsp源代码文件添加licence信息头");
        }
        if(wrong.size()>0){
            System.out.println("Jsp源代码错误个数 "+wrong.size());
            wrong.forEach(w -> {
                System.out.println("        "+w);
            });
            throw new RuntimeException("错误：为Jsp源代码文件添加licence信息头");
        }        
    }
    public static void processJsFile(){
        //重置计算器
        count = 0;
        fail = new ArrayList<>();
        
        String JSLICENSE=JAVALICENSE;
        addLicenceForJsFile(new File(APDPLATPATH),JSLICENSE);
        System.out.println("为 "+count+" 个js源代码文件添加licence信息头");
        if(fail.size()>0){
            System.out.println("处理失败个数 "+fail.size());
            fail.forEach(f -> {
                System.out.println("        "+f);
            });
            throw new RuntimeException("失败：为JS源代码文件添加licence信息头");
        }    
    }
    /**
     * 给JAVA源代码文件统一地添加licence信息头
     * @param path 源码所处的根目录
     * @param licence 许可证信息（在netbeans中复制一段文本粘贴到变量的双引号内，IDE自动格式化，相当赞）
     */
    private static void addLicenceForJavaFile(File path, String licence) {
        if (path != null && path.exists()) {
            //处理文件夹
            if (path.isDirectory()) {
                String[] children = path.list();
                for (int i = 0; i < children.length; i++) {
                    File child = new File(path.getPath() + System.getProperty("file.separator") + children[i]);
                    //递归处理
                    addLicenceForJavaFile(child, licence);
                }
            } else {
                //处理java文件
                if (path.getName().toLowerCase().endsWith(".java")) {
                    System.out.println(path.getAbsolutePath());
                    count++;
                    try {
                        byte[] content;
                        try (RandomAccessFile f = new RandomAccessFile(path, "rw")) {
                            content = new byte[ (int) f.length()];
                            f.readFully(content);
                        }
                        String text = new String(content,"utf-8");
                        text = text.trim();
                        while (text.startsWith("/n")) {
                            text = text.substring(1);
                        }
                        //如果已经有同样的licence，则忽略
                        int pos = text.indexOf(licence);
                        if(pos!=-1){
                            return;
                        }
                        //有package声明的，保留package以后的内容
                        if (text.indexOf("package") != -1) {
                            text = text.substring(text.indexOf("package"));
                        }
                        //没有package声明的，有import声明的，保留import以后的内容
                        else if (text.indexOf("package") == -1 && text.indexOf("import") != -1) {
                            text = text.substring(text.indexOf("import"));
                        }
                        //没有package声明也没有import声明的，有类级别注释的，则保留类级别注释以后的内容
                        else if (text.indexOf("package") == -1 && text.indexOf("import") == -1 && text.indexOf("/**") != -1 && text.indexOf("public class") != -1 && text.indexOf("/**")<text.indexOf("public class") ) {
                            text = text.substring(text.indexOf("/**"));
                        }
                        //没有package声明也没有import声明的，也没有类级别注释的则保留public class以后的内容
                        else if (text.indexOf("package") == -1 && text.indexOf("import") == -1 && text.indexOf("public class") != -1 && ( text.indexOf("/**")>text.indexOf("public class") || text.indexOf("/**")==-1 )) {
                            text = text.substring(text.indexOf("public class"));
                        }else{
                            wrong.add(path.getAbsolutePath());
                            return;
                        }
                        try (Writer writer = new OutputStreamWriter(new FileOutputStream(path),"utf-8")) {
                            writer.write(licence);
                            writer.write("\n\n");
                            writer.write(text);
                        }
                    }
                    catch (Exception ex) {
                        fail.add(path.getAbsolutePath());
                    }
                }
            }
        }
    }
    
    /**
     * 给Jsp源代码文件统一地添加licence信息头
     * @param path 源码所处的根目录
     * @param licence 许可证信息（在netbeans中复制一段文本粘贴到变量的双引号内，IDE自动格式化，相当赞）
     */
    private static void addLicenceForJspFile(File path, String licence) {
        if (path != null && path.exists()) {
            //处理文件夹
            if (path.isDirectory()) {
                String[] children = path.list();
                for (int i = 0; i < children.length; i++) {
                    File child = new File(path.getPath() + System.getProperty("file.separator") + children[i]);
                    //递归处理
                    addLicenceForJspFile(child, licence);
                }
            } else {
                //处理jsp文件
                if (path.getName().toLowerCase().endsWith(".jsp") && path.getAbsolutePath().toLowerCase().indexOf("target")==-1) {
                    System.out.println(path.getAbsolutePath());
                    count++;
                    try {
                        byte[] content;
                        try (RandomAccessFile f = new RandomAccessFile(path, "rw")) {
                            content = new byte[ (int) f.length()];
                            f.readFully(content);
                        }
                        String text = new String(content,"utf-8");
                        text = text.trim();
                        while (text.startsWith("/n")) {
                            text = text.substring(1);
                        }
                        //如果已经有同样的licence，则忽略
                        int pos = text.indexOf(licence);
                        if(pos!=-1){
                            return;
                        }
                        //有page声明的，保留page以后的内容
                        if (text.indexOf("<%@page") != -1) {
                            text = text.substring(text.indexOf("<%@page"));
                        }else{
                            wrong.add(path.getAbsolutePath());
                            return;
                        }
                        try (Writer writer = new OutputStreamWriter(new FileOutputStream(path),"utf-8")) {
                            writer.write(licence);
                            writer.write("\n\n");
                            writer.write(text);
                        }
                    }
                    catch (Exception ex) {
                        fail.add(path.getAbsolutePath());
                    }
                }
            }
        }
    }
    
    /**
     * 给js源代码文件统一地添加licence信息头
     * @param path 源码所处的根目录
     * @param licence 许可证信息（在netbeans中复制一段文本粘贴到变量的双引号内，IDE自动格式化，相当赞）
     */
    private static void addLicenceForJsFile(File path, String licence) {
        if (path != null && path.exists()) {
            //处理文件夹
            if (path.isDirectory()) {
                //判断目录是否被排除
                String dir = path.getAbsolutePath();
                for(String exclude : jsExcludes){
                    if(dir.endsWith(exclude)){
                        System.out.println("目录 "+dir+" 不在LICENSE处理范围内");
                        return;
                    }
                }
                String[] children = path.list();
                for (int i = 0; i < children.length; i++) {
                    File child = new File(path.getPath() + System.getProperty("file.separator") + children[i]);
                    //递归处理
                    addLicenceForJsFile(child, licence);
                }
            } else {
                //处理js文件
                if (path.getName().toLowerCase().endsWith(".js")) {                    
                    //判断文件是否被排除
                    String absPath = path.getAbsolutePath();
                    for(String exclude : jsExcludes){
                        if(absPath.endsWith(exclude)){
                            System.out.println("文件 "+absPath+" 不在LICENSE处理范围内");
                            return;
                        }
                    }
                    
                    System.out.println(path.getAbsolutePath());
                    count++;
                    try {
                        byte[] content;
                        try (RandomAccessFile f = new RandomAccessFile(path, "rw")) {
                            content = new byte[ (int) f.length()];
                            f.readFully(content);
                        }
                        String text = new String(content,"utf-8");
                        text = text.trim();
                        while (text.startsWith("/n")) {
                            text = text.substring(1);
                        }
                        //如果已经有同样的licence，则忽略
                        int pos = text.indexOf(licence);
                        if(pos!=-1){
                            return;
                        }
                        try (Writer writer = new OutputStreamWriter(new FileOutputStream(path),"utf-8")) {
                            writer.write(licence);
                            writer.write("\n\n");
                            writer.write(text);
                        }
                    }
                    catch (Exception ex) {
                        fail.add(path.getAbsolutePath());
                    }
                }
            }
        }
    }
    
}