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
/**
 * 改变APDPlat源代码的包名
 * 
 * 需要更改package
 * 需要更改import
 * 需要更改/分割的类路径资源
 * 需要更改文件夹名称
 * ......
 * 
 * @author 杨尚川
 */
public class ChangePackageName {
    private static int fileCount = 0;
    private static int failCount = 0;
    
    private static int folderRenameSuccessCount = 0;
    private static int folderRenameFailCount = 0;
    
    private static final String APDPLATPATH="D:\\Workspaces\\NetBeansProjects\\APDPlat";        
    
    public static void main(String[] args) {
        //processJavaFile("org.apdplat","com.apdplat");
        processJavaFile("com.apdplat","org.apdplat");
    }
    public static void processJavaFile(String oldPackage, String newPackage){  
        replaceFolderForJavaFile(new File(APDPLATPATH), oldPackage, newPackage);
        replacePackageForJavaFile(new File(APDPLATPATH), oldPackage, newPackage);
        System.out.println("oldPackage: "+oldPackage);   
        System.out.println("newPackage: "+newPackage);   
        System.out.println("成功为： "+fileCount+" 个源代码文件更改包名");   
        if(failCount>0){
            System.out.println("失败为： "+failCount+" 个源代码文件更改包名");     
        }
        
        System.out.println("成功改名folder： "+folderRenameSuccessCount);    
        if(folderRenameFailCount>0){
            System.out.println("失败改名folder： "+folderRenameFailCount);    
        }
    }
    
    private static void replaceFolderForJavaFile(File path, String oldPackage, String newPackage) {
        if (path != null && path.exists()) {
            //处理文件夹
            if (path.isDirectory()) {
                if("target".equals(path.getName())){
                    return;
                }
                File p=path.getParentFile();
                if(p!=null){
                    File pp=p.getParentFile();
                    if(pp!=null){
                        File ppp=pp.getParentFile();            
                        if(ppp!=null){
                            if( ( "java".equals(p.getName()) && "main".equals(pp.getName()) && "src".equals(ppp.getName()) ) || 
                                    ( "java".equals(p.getName()) && "test".equals(pp.getName()) && "src".equals(ppp.getName()) ) || 
                                    ( "resources".equals(p.getName()) && "main".equals(pp.getName()) && "src".equals(ppp.getName()) ) || 
                                    ( "resources".equals(p.getName()) && "test".equals(pp.getName()) && "src".equals(ppp.getName()) ) ){
                                if(path.getName().equals(oldPackage.split("\\.")[0])){
                                    //改一级目录
                                    File dest=new File(path.getAbsolutePath().replace(oldPackage.split("\\.")[0], newPackage.split("\\.")[0]));
                                    boolean success = path.renameTo(dest);
                                    if(success){
                                        System.out.println("成功替换一级目录");
                                        folderRenameSuccessCount++;
                                        File[] sub=dest.listFiles();
                                        if(sub!=null && sub.length==1){
                                            File second=sub[0];
                                            if(second.getName().equals(oldPackage.split("\\.")[1])){
                                                dest=new File(second.getAbsolutePath().replace(oldPackage.split("\\.")[1], newPackage.split("\\.")[1]));
                                                success = second.renameTo(dest);
                                                if(!success){
                                                    folderRenameFailCount++;
                                                    throw new RuntimeException("更改二级文件夹失败");
                                                }
                                                System.out.println("成功替换二级目录");
                                                folderRenameSuccessCount++;
                                            }else{
                                                System.out.println("不需要替换二级目录");
                                            }
                                        }
                                    }else{
                                        folderRenameFailCount++;
                                        throw new RuntimeException("更改一级文件夹失败");
                                    }
                                }else{
                                    System.out.println("不需要替换一级目录");
                                }
                            }  
                        }
                    }
                }
                //检查path是否已经改名
                //如果改名了就不用管了
                if(path!=null){
                    String[] children = path.list();
                    if(children!=null){
                        for (int i = 0; i < children.length; i++) {
                            File child = new File(path.getPath() + System.getProperty("file.separator") + children[i]);
                            //递归处理
                            if(child.isDirectory()){
                                replaceFolderForJavaFile(child, oldPackage, newPackage);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private static void replacePackageForJavaFile(File path, String oldPackage, String newPackage) {
        if (path != null && path.exists()) {
            //处理文件夹
            if (path.isDirectory()) {
                if("target".equals(path.getName())){
                    return;
                }
                String[] children = path.list();
                for (int i = 0; i < children.length; i++) {
                    File child = new File(path.getPath() + System.getProperty("file.separator") + children[i]);
                    //递归处理
                    replacePackageForJavaFile(child, oldPackage, newPackage);
                }
            } else {
                //处理相关文件
                if ( path.getName().toLowerCase().endsWith(".java") ||
                        path.getName().toLowerCase().endsWith(".jsp")  || 
                        path.getName().toLowerCase().endsWith(".ftl") || 
                        path.getName().toLowerCase().endsWith(".properties")  || 
                        path.getName().toLowerCase().endsWith(".xml") ) {
                    System.out.println(path.getAbsolutePath());
                    fileCount++;
                    try {
                        byte[] content;
                        try (RandomAccessFile f = new RandomAccessFile(path, "rw")) {
                            content = new byte[ (int) f.length()];
                            f.readFully(content);
                        }
                        String text = new String(content,"utf-8");
                        
                        //替换package
                        text=text.replaceAll("package "+oldPackage, "package "+newPackage);
                        //替换import
                        text=text.replaceAll("import "+oldPackage, "import "+newPackage);
                        //替换/分割的路径        
                        String _old=oldPackage.split("\\.")[0]+"/"+oldPackage.split("\\.")[1];
                        String _new=newPackage.split("\\.")[0]+"/"+newPackage.split("\\.")[1];
                        text=text.replaceAll(_old, _new);
                        //除了本文件，其他文件要全文替换，这个替换要在最后
                        if (!path.getName().endsWith("ChangePackageName.java")){
                            text=text.replaceAll(oldPackage, newPackage);
                        }
                        try (Writer writer = new OutputStreamWriter(new FileOutputStream(path),"utf-8")) {
                            writer.write(text);
                        }
                    }
                    catch (Exception ex) {
                        failCount++;
                    }
                }
            }
        }
    }
}