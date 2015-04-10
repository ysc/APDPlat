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

package org.apdplat.module.dictionary.service;

import org.apdplat.module.dictionary.model.Dic;
import org.apdplat.module.dictionary.model.DicItem;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.apdplat.platform.util.FileUtils;
import org.apdplat.platform.util.XMLFactory;
import org.apdplat.platform.util.XMLUtils;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author 杨尚川
 */
public class DicParser {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(DicParser.class);
    private static final String dtdFile="/target/dic.dtd";
    /**
     * 返回所有Dic对象中dicItems不为空的Dic对象
     * @return 
     */
    public static List<Dic> getLeafDics(){
        List<Dic> dics=getDics();
        List<Dic> result=new ArrayList<>();
        dics.forEach(dic -> {
            execute(result,dic);
        });
        return result;
    }
    private static void execute(List<Dic> result,Dic dic){
        if(dic.getDicItems().size()>0){
            result.add(dic);
        }
        dic.getSubDics().forEach(item -> {
            execute(result,item);
        });
    }
    
    /**
     * 将多个META-INF/services/dic.xml文件进行解析，每一个文件对应一个Dic对象
     * @return 
     */
    public static List<Dic> getDics(){
        List<Dic> dics=new ArrayList<>();
        //准备数据字典DTD文件以供校验使用
        prepareDtd();
        try{
            Enumeration<URL> ps = Thread.currentThread().getContextClassLoader().getResources("META-INF/services/dic.xml");
            while(ps.hasMoreElements()) {
                URL url=ps.nextElement();
                LOG.info("找到数据字典描述文件(Find data dictionary description file)："+url.getPath());
                try(InputStream in = url.openStream()) {
                    byte[] data=FileUtils.readAll(in);
                    String xml=new String(data,"utf-8");
                    LOG.info("将DTD文件替换为绝对路径(Replace DID file into absolute path)");
                    xml=xml.replace("dic.dtd", FileUtils.getAbsolutePath(dtdFile));
                    LOG.info("将数据字典文件读取到字节数组中，长度为(Load data dictionary into byte array, the length is)："+data.length);
                    ByteArrayInputStream bin=new ByteArrayInputStream(xml.getBytes("utf-8"));    
                    LOG.info("注册dic.xml文件(Register dic.xml file)");
                    LOG.info("验证dic.xml文件(Verify dic.xml file )");
                    //校验文件
                    verifyFile(bin);
                    // 解析数据字典
                    parseDic(xml,dics);
                }catch(Exception e){
                    LOG.error("解析数据字典出错(Error in parsing the data dictionary)",e);
                }
            }            
        }catch(Exception e){
            LOG.error("解析数据字典出错(Error in parsing the data dictionary)",e);
        }
        return dics;
    }    
    private static void parseDic(String xml, List<Dic> dics) {
        XMLFactory factory=new XMLFactory(Dic.class,DicItem.class);
        Dic dic=factory.unmarshal(xml);
        //将XML表示的数据字典转变为JAVA对象表示的数据字典
        assembleDic(dic);
        dics.add(dic);
    }
    /**
     * 采用递归的方式建立数据字典树的引用关系
     * @param dic  数据字典树的根
     */
    private static void assembleDic(Dic dic){
        AtomicInteger i = new AtomicInteger();
        dic.getDicItems().forEach(item -> {
            item.setDic(dic);
            if(item.getOrderNum()==0){
                item.setOrderNum(i.incrementAndGet());
            }
            if(item.getCode()==null){
                item.setCode(i.toString());
            }
        });
        dic.getSubDics().forEach(d -> {
            d.setParentDic(dic);
            assembleDic(d);
        });
    }
    private static void prepareDtd(){
        try{
            Enumeration<URL> ps = Thread.currentThread().getContextClassLoader().getResources("META-INF/services/dic.dtd");
            if(ps.hasMoreElements()) {
                URL url=ps.nextElement();
                LOG.info("找到数据字典DTD文件(Find data dictionary DID file)："+url.getPath());
                try(InputStream in = url.openStream()) {
                    byte[] data=FileUtils.readAll(in);
                    FileUtils.createAndWriteFile(dtdFile, data);
                    LOG.info("将DTD复制到(Copy DID to)："+dtdFile);
                }catch(Exception e){
                    LOG.error("解析数据字典出错(Error in parsing the data dictionary)",e);
                }
            }else{
                LOG.info("没有找到数据字典DTD文件(Miss the data dictionary DID file)");
            }            
        }catch(Exception e){
            LOG.error("解析数据字典出错(Error in parsing the data dictionary)",e);
        }
    }
    private static void verifyFile(InputStream in){    
        boolean pass=XMLUtils.validateXML(in);
        if(!pass){
            LOG.info("验证没有通过，请参考dic.dtd文件(Validation failed, please refer to dic.dtd file)");
            return ;
        }
        LOG.info("验证通过(Validation succeed)");
    }
    private static void print(Dic dic,String pre){
        System.out.println(pre+dic.getChinese()+":"+dic.getEnglish());
        dic.getDicItems().forEach(item -> {
            System.out.println(pre+"    "+item.getCode()+":"+item.getName()+":"+item.getOrderNum());
        });
        dic.getSubDics().forEach(sub -> {
            print(sub,pre+"     ");
        });
    }
    public static void main(String[] args){
        getDics().forEach(dic -> {
            print(dic,"");
        });
    }
}