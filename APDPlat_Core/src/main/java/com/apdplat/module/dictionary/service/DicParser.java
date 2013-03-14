package com.apdplat.module.dictionary.service;

import com.apdplat.module.dictionary.model.Dic;
import com.apdplat.module.dictionary.model.DicItem;
import com.apdplat.platform.log.APDPlatLogger;
import com.apdplat.platform.util.FileUtils;
import com.apdplat.platform.util.XMLFactory;
import com.apdplat.platform.util.XMLUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 *
 * @author ysc
 */
public class DicParser {
    protected static final APDPlatLogger log = new APDPlatLogger(DicParser.class);
    private static final String dtdFile="/target/dic.dtd";
    /**
     * 返回所有Dic对象中dicItems不为空的Dic对象
     * @return 
     */
    public static List<Dic> getLeafDics(){
        List<Dic> dics=getDics();
        List<Dic> result=new ArrayList<>();
        for(Dic dic : dics){
            execute(result,dic);
        }
        return result;
    }
    private static void execute(List<Dic> result,Dic dic){
        if(dic.getDicItems().size()>0){
            result.add(dic);
        }
        for(Dic item : dic.getSubDics()){
            execute(result,item);
        }
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
                InputStream in = null;
                try {
                    URL url=ps.nextElement();
                    log.info("找到数据字典描述文件(Find data dictionary description file)："+url.getPath());
                    in = url.openStream();
                    byte[] data=FileUtils.readAll(in);
                    String xml=new String(data,"utf-8");
                    log.info("将DTD文件替换为绝对路径(Replace DID file into absolute path)");
                    xml=xml.replace("dic.dtd", FileUtils.getAbsolutePath(dtdFile));
                    log.info("将数据字典文件读取到字节数组中，长度为(Load data dictionary into byte array, the length is)："+data.length);
                    ByteArrayInputStream bin=new ByteArrayInputStream(xml.getBytes("utf-8"));    
                    log.info("注册dic.xml文件(Register dic.xml file)");
                    log.info("验证dic.xml文件(Verify dic.xml file )");
                    //校验文件
                    verifyFile(bin);
                    // 解析数据字典
                    parseDic(xml,dics);
                }catch(Exception e)
                {
                    log.error("解析数据字典出错(Error in parsing the data dictionary)",e);
                }
                finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        log.error("解析数据字典出错(Error in parsing the data dictionary)",e);
                    }
                }
            }            
        }catch(Exception e){
            log.error("解析数据字典出错(Error in parsing the data dictionary)",e);
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
        Integer i=1;
        for(DicItem c : dic.getDicItems()){
            c.setDic(dic);
            if(c.getOrderNum()==0){
                c.setOrderNum(i++);
            }
            if(c.getCode()==null){
                c.setCode(i.toString());
            }
        }
        for(Dic d : dic.getSubDics()){
            d.setParentDic(dic);
            assembleDic(d);
        }
    }
    private static void prepareDtd(){
        try{
            Enumeration<URL> ps = Thread.currentThread().getContextClassLoader().getResources("META-INF/services/dic.dtd");
            if(ps.hasMoreElements()) {
                InputStream in = null;
                try {
                    URL url=ps.nextElement();
                    log.info("找到数据字典DTD文件(Find data dictionary DID file)："+url.getPath());
                    in = url.openStream();
                    byte[] data=FileUtils.readAll(in);
                    log.info("将DTD复制到(Copy DID to)："+dtdFile);
                    FileUtils.createAndWriteFile(dtdFile, data);
                }catch(Exception e)
                {
                    log.error("解析数据字典出错(Error in parsing the data dictionary)",e);
                }
                finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        log.error("解析数据字典出错(Error in parsing the data dictionary)",e);
                    }
                }
            }else{
                log.info("没有找到数据字典DTD文件(Miss the data dictionary DID file)");
            }            
        }catch(Exception e){
            log.error("解析数据字典出错(Error in parsing the data dictionary)",e);
        }
    }
    private static void verifyFile(InputStream in){    
        boolean pass=XMLUtils.validateXML(in);
        if(!pass){
            log.info("验证没有通过，请参考dic.dtd文件(Validation failed, please refer to dic.dtd file)");
            return ;
        }
        log.info("验证通过(Validation succeed)");
    }
    private static void print(Dic dic,String pre){
        System.out.println(pre+dic.getChinese()+":"+dic.getEnglish());
        for(DicItem item : dic.getDicItems()){
            System.out.println(pre+"    "+item.getCode()+":"+item.getName()+":"+item.getOrderNum());
        }
        for(Dic sub : dic.getSubDics()){
            print(sub,pre+"     ");
        }
    }
    public static void main(String[] args){
        for(Dic dic : getDics()){
            print(dic,"");
        }
    }
}
