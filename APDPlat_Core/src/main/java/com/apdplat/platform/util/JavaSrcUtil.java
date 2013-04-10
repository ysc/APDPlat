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

package com.apdplat.platform.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 *统计源代码行数
 * 将所有源代码写到一个文件
 * @author 杨尚川
 */
public class JavaSrcUtil {
	
	private static final String FILE_TYPE = "java";
	
	private long rows = 0;
	
	private StringBuffer sbBuffer = new StringBuffer();

	/**
	 * 根据文件计算代码行数
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public long staticRowsByFile(File file) throws IOException {
		if (file.isDirectory()) { // 非文件
			throw new IOException("is not file:" + file);
		} else if (!file.getName().endsWith("." + FILE_TYPE)) { // 非java文件
			return 0;
		}
		
		long count = 0;
		
		FileInputStream fis = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		
		String str = null;
		while ((str = br.readLine()) != null) {
			str = str.trim();
			if (str.length() > 1 && !str.startsWith("/") && !str.startsWith("*")) {
				count ++;
			}
		}
		
		return count;
	}
	
	/**
	 * 根据目录计算目录以内的代码行数
	 * @param dirFile
	 * @return
	 * @throws IOException
	 */
	public long staticRowsByDirectory(File dirFile) throws IOException {
		if (!dirFile.isDirectory()) {
			throw new IOException("is not Directory:" + dirFile);
		}
		
		File[] files = dirFile.listFiles();
		for (File childFile : files) {
			if (childFile.isDirectory()) {
				staticRowsByDirectory(childFile);
			} else {
				rows += staticRowsByFile(childFile);
			}
		}
		return rows;
	}
	
	/**
	 * 获取文件代码
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public String getSrcByFile(File file) throws IOException {
		if (file.isDirectory()) { // 非文件
			throw new IOException("is not file:" + file);
		} else if (!file.getName().endsWith("." + FILE_TYPE)) { // 非java文件
			return "";
		}
		
		StringBuilder temp = new StringBuilder();
		
		FileInputStream fis = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		
		String str = null;
		while ((str = br.readLine()) != null) {
			if (str.trim().length() > 0) {
				temp.append(str);
				temp.append("\r\n");
			}
		}
		
		return temp.toString();
	}
	
	/**
	 * 获取目录以内所有的源代码
	 * @param dirFile
	 * @return
	 * @throws IOException
	 */
	public String getSrcByDirectory(File dirFile) throws IOException {
		if (!dirFile.isDirectory()) {
			throw new IOException("is not Directory:" + dirFile);
		}
		
		File[] files = dirFile.listFiles();
		for (File childFile : files) {
			if (childFile.isDirectory()) {
				getSrcByDirectory(childFile);
			} else {
				sbBuffer.append(getSrcByFile(childFile));
			}
		}
		return sbBuffer.toString();
	}
	
	public static void main(String[] args) throws IOException {
		File file = new File("D:/Workspaces/NetBeansProjects/APDPlat");
		JavaSrcUtil jsu = new JavaSrcUtil();
		System.out.println(jsu.staticRowsByDirectory(file));
		
		OutputStream os = new FileOutputStream(new File("d:/APDPlat.java"));
		os.write(jsu.getSrcByDirectory(file).getBytes());
	}
}