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

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static junit.framework.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author 杨尚川
 */
public class FileUtilsTest {

    @Before
    public void setUp() {    
        FileUtils.setBasePath(System.getProperty("user.dir"));
    }
    @Test
    public void testCreateAndWriteFile() {
        System.out.println("testCreateAndWriteFile");
        String file="target/testFile.txt";
        
        String str1="APDPlat应用级开发平台（杨尚川）";
        FileUtils.createAndWriteFile(file, str1);
        Collection<String> result1=FileUtils.getTextFileContent(file);
        assertEquals(1, result1.size());
        assertTrue(result1.contains(str1));     
        
        String str2="APDPlat应用级开发平台（ysc）";
        FileUtils.createAndWriteFile(file, str2);
        Collection<String> result2=FileUtils.getTextFileContent(file);
        assertEquals(1, result2.size());
        assertTrue(result2.contains(str2));     
        
        String str3="APDPlat应用级开发平台（281032878@qq.com）";
        FileUtils.createAndWriteFile(file, str3);
        Collection<String> result3=FileUtils.getTextFileContent(file);
        assertEquals(1, result3.size());
        assertTrue(result3.contains(str3));     
    }

    @Test
    public void testAppendText() {
        System.out.println("testAppendText");
        String file="target/testAppendText-"+System.nanoTime()+".txt";
        
        String text="APDPlat应用级开发平台";
        FileUtils.appendText(file, text);
        Collection<String> result1=FileUtils.getTextFileContent(file);
        assertEquals(1, result1.size());
        assertTrue(result1.contains(text));   
                
        String text2="ysc is the author";
        FileUtils.appendText(file, text2);
        Collection<String> result2=FileUtils.getTextFileContent(file);
        assertEquals(1, result2.size());
        assertTrue(result2.contains(text+text2));
        
        String text3="APDPlat is the Abbreviation of Application Product Development Platform";
        FileUtils.appendText(file, text3);
        Collection<String> result3=FileUtils.getTextFileContent(file);
        assertEquals(1, result3.size());
        assertTrue(result3.contains(text+text2+text3));   
    }

    @Test
    public void testAppendTextInNewLine() {
        System.out.println("testAppendTextInNewLine");
        String file="target/testAppendTextInNewLine-"+System.nanoTime()+".txt";
        
        String line1="APDPlat应用级开发平台";
        FileUtils.appendTextInNewLine(file, line1);
        Collection<String> result1=FileUtils.getTextFileContent(file);
        assertEquals(1, result1.size());
        assertTrue(result1.contains(line1));       
        
        String line2="ysc is the author";
        FileUtils.appendTextInNewLine(file, line2);
        Collection<String> result2=FileUtils.getTextFileContent(file);
        assertEquals(2, result2.size());
        assertTrue(result2.contains(line2));
        
        String line3="APDPlat is the Abbreviation of Application Product Development Platform";
        FileUtils.appendTextInNewLine(file, line3);
        Collection<String> result3=FileUtils.getTextFileContent(file);
        assertEquals(3, result3.size());
        assertTrue(result3.contains(line3));
    }
    @Test
    public void testGetAbsolutePath1() {
        System.out.println("testGetAbsolutePath1");
        //Linux平台
        String basePath = "/home/ysc";
        FileUtils.setBasePath(basePath);
        
        List<String> list = new ArrayList<>();
        list.add("/");
        list.add("/target");
        list.add("/target/getAbsolutePath.txt");
        list.add("target");
        list.add("target/getAbsolutePath.txt");
        
        List<String> expected = new ArrayList<>();
        expected.add("/home/ysc/");
        expected.add("/home/ysc/target");
        expected.add("/home/ysc/target/getAbsolutePath.txt");
        expected.add("/home/ysc/target");
        expected.add("/home/ysc/target/getAbsolutePath.txt");
        
        int len = list.size();
        for(int i=0;i<len;i++){
            String result=FileUtils.getAbsolutePath(list.get(i));
            assertEquals(expected.get(i),result);
        }
    }
    @Test
    public void testGetAbsolutePath2() {
        System.out.println("testGetAbsolutePath2");
        //Linux平台
        String basePath = "/home/ysc/";
        FileUtils.setBasePath(basePath);
        
        List<String> list = new ArrayList<>();
        list.add("/");
        list.add("/target");
        list.add("/target/getAbsolutePath.txt");
        list.add("target");
        list.add("target/getAbsolutePath.txt");
        
        List<String> expected = new ArrayList<>();
        expected.add("/home/ysc/");
        expected.add("/home/ysc/target");
        expected.add("/home/ysc/target/getAbsolutePath.txt");
        expected.add("/home/ysc/target");
        expected.add("/home/ysc/target/getAbsolutePath.txt");
        
        int len = list.size();
        for(int i=0;i<len;i++){
            String result=FileUtils.getAbsolutePath(list.get(i));
            assertEquals(expected.get(i),result);
        }
    }
    @Test
    public void testGetAbsolutePath3() {
        System.out.println("testGetAbsolutePath3");
        //Windows平台
        String basePath = "c:\\test\\base";
        FileUtils.setBasePath(basePath);
        
        List<String> list = new ArrayList<>();
        list.add("c:\\test\\test.txt");
        list.add("c:/test/test.txt");
        list.add("/");
        list.add("/target");
        list.add("/target/getAbsolutePath.txt");
        list.add("target");
        list.add("target/getAbsolutePath.txt");
        
        List<String> expected = new ArrayList<>();
        expected.add("c:/test/test.txt");
        expected.add("c:/test/test.txt");
        expected.add("c:/test/base/");
        expected.add("c:/test/base/target");
        expected.add("c:/test/base/target/getAbsolutePath.txt");
        expected.add("c:/test/base/target");
        expected.add("c:/test/base/target/getAbsolutePath.txt");
        
        int len = list.size();
        for(int i=0;i<len;i++){
            String result=FileUtils.getAbsolutePath(list.get(i));
            assertEquals(expected.get(i),result);
        }
    }
    @Test
    public void testGetAbsolutePath4() {
        System.out.println("testGetAbsolutePath4");
        //Windows平台
        String basePath = "c:\\test\\base\\";
        FileUtils.setBasePath(basePath);
        
        List<String> list = new ArrayList<>();
        list.add("c:\\test\\test.txt");
        list.add("c:/test/test.txt");
        list.add("/");
        list.add("/target");
        list.add("/target/getAbsolutePath.txt");
        list.add("target");
        list.add("target/getAbsolutePath.txt");
        
        List<String> expected = new ArrayList<>();
        expected.add("c:/test/test.txt");
        expected.add("c:/test/test.txt");
        expected.add("c:/test/base/");
        expected.add("c:/test/base/target");
        expected.add("c:/test/base/target/getAbsolutePath.txt");
        expected.add("c:/test/base/target");
        expected.add("c:/test/base/target/getAbsolutePath.txt");
        
        int len = list.size();
        for(int i=0;i<len;i++){
            String result=FileUtils.getAbsolutePath(list.get(i));
            assertEquals(expected.get(i),result);
        }
    }
    @Test
    public void testCopyFile() {
        System.out.println("testCopyFile");
        String file="target/testCopyFile.txt";
        String dist="target/testCopyFileDist.txt";
        
        String str="APDPlat应用级开发平台（杨尚川）";
        FileUtils.createAndWriteFile(file, str);
        FileUtils.copyFile(new File(file), new File(dist));
        
        Collection<String> result=FileUtils.getTextFileContent(dist);
        assertEquals(1, result.size());
        assertTrue(result.contains(str));           
    }
    @Test
    public void testReadAll() throws UnsupportedEncodingException {
        System.out.println("readAll");
        String file="target/testReadAll.txt";
        
        String str="APDPlat应用级开发平台（杨尚川）";
        FileUtils.createAndWriteFile(file, str);
        byte[] data=FileUtils.readAll(new File(file));
        
        assertEquals(str, new String(data,"utf-8"));
    }
    @Test
    public void testExistsFile() throws UnsupportedEncodingException {
        System.out.println("testExistsFile");
        String file="target/testExistsFile.txt";
        
        String str="APDPlat应用级开发平台（杨尚川）";
        FileUtils.createAndWriteFile(file, str);
        
        assertEquals(true, FileUtils.existsFile(file));
    }
    @Test
    public void testRemoveFile() throws UnsupportedEncodingException {
        System.out.println("testRemoveFile");
        String file="target/testRemoveFile.txt";
        
        String str="APDPlat应用级开发平台（杨尚川）";
        FileUtils.createAndWriteFile(file, str);
        
        assertEquals(true, FileUtils.existsFile(file));
        FileUtils.removeFile(file);
        assertEquals(false, FileUtils.existsFile(file));
    }
}