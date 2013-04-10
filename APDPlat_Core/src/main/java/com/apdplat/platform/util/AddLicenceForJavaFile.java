/**
 * 
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川
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

package com.apdplat.platform.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
/**
 * 给JAVA源代码文件统一地添加licence信息头
 * 检查文件package、import、类级别注释、是否有public class
 * 用到了Java7的新特性，强大
 * @author ysc
 */
public class AddLicenceForJavaFile {
    private static int count = 0;
    private static List<String> fail = new ArrayList<>();
    private static List<String> wrong = new ArrayList<>();

    public static void main(String[] args) {
        String licence="/**\n" +
" * \n" +
" * APDPlat - Application Product Development Platform\n" +
" * Copyright (c) 2013, 杨尚川\n" +
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
        addLicenceForJavaFile(new File("D:\\Workspaces\\NetBeansProjects\\APDPlat"),licence);
        System.out.println("为 "+count+" 个Java源代码文件添加licence信息头");
        if(fail.size()>0){
            System.out.println("处理失败个数 "+fail.size());
            for(String f : fail){
                System.out.println("        "+f);
            }
        }
        if(wrong.size()>0){
            System.out.println("JAVA源代码错误个数 "+wrong.size());
            for(String w : wrong){
                System.out.println("        "+w);
            }
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
                        String text = new String(content);
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
}