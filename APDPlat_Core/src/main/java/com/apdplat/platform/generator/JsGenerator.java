package com.apdplat.platform.generator;

import com.apdplat.module.system.service.PropertyHolder;
import com.apdplat.module.system.service.SystemListener;
import com.apdplat.platform.model.Model;
import com.apdplat.platform.model.ModelFieldData;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Entity;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

/**
 *
 * @author ysc
 */
public class JsGenerator  extends Generator{    
    private final static ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        
    private static Configuration freemarkerConfiguration = null;

    static {
        factory.setTemplateLoaderPath(PropertyHolder.getProperty("action.generator.freemarker.template"));
        try {
            freemarkerConfiguration = factory.createConfiguration();
        } catch (IOException | TemplateException e) {
            log.error("初始化模板错误",e);
        }

        try {
            System.out.println("扫描模型");
            SystemListener.prepareForSpring();
            String[] basePackages=System.getProperty("basePackage").split(",");
            
            for(String basePackage : basePackages){
                String pattern="classpath*:"+basePackage+"/**/*.class";
                System.out.println("模式："+pattern);
                Resource[] rs= resourcePatternResolver.getResources(pattern);
                System.out.println("扫描到的数量为："+rs.length);
                
                for(Resource r : rs){
                    try {
                        String path=r.getURL().getPath();
                        if(!path.contains("/model/")){
                            continue;
                        }
                        int index=path.indexOf("!/");
                        String clazz="";
                        if(index!=-1){
                            clazz=path.substring(index+2);
                        }else{
                            index=path.indexOf(basePackage);
                            clazz=path.substring(index);
                        }
                        clazz=clazz.replace("/", ".").replace(".class", "");
                        
                        Class cls=Class.forName(clazz);
                        if(cls.isAnnotationPresent(Entity.class)){
                            String name=Character.toLowerCase(cls.getSimpleName().charAt(0))+cls.getSimpleName().substring(1);
                            Model obj=(Model)cls.newInstance();
                            actionToModel.put(name, obj);
                            System.out.println(path);
                            System.out.println(clazz);
                        }
                    } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                        log.error("生成JS错误",e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("生成JS错误",e);
        }
        
    }
    /**
     * 
     * @param modulePath 多级非叶子模块（路径名称等于模块英文名称）
     * @param module 叶子模块（路径名称等于模块英文名称）
     * @return 
     */
    public static String getContent(String modulePath, String module) {
        Model model=actionToModel.get(module);
        if(model==null){
            return "";
        }
        
        String templateName="js.ftl";

        log.info("开始生成JS");
        
        Map<String, Object> context = new HashMap<>();
        List<ModelFieldData> attrs=model.getAllModelAttr();
        
        int baseHeight=120;
        //添加，分为两列
        List<ModelFieldData> modelAttrs=model.getModelAttr();
        int len=modelAttrs.size()/2+modelAttrs.size()%2;
        int createHeight=baseHeight+len*16*2;        
        int labelWidth=80;
        int maxLength=0;
        List<ModelFieldData> leftModelAttrs=new ArrayList<>();
        for(int i=0;i<len;i++){
            ModelFieldData data=modelAttrs.get(i);
            leftModelAttrs.add(data);
            int length=data.getChinese().length();
            if(length>4){
                maxLength=length>maxLength?length:maxLength;
            }
        }
        List<ModelFieldData> rightModelAttrs=new ArrayList<>();
        for(int i=len;i<modelAttrs.size();i++){
            ModelFieldData data=modelAttrs.get(i);
            rightModelAttrs.add(data);
            int length=data.getChinese().length();
            if(length>4){
                maxLength=length>maxLength?length:maxLength;
            }
        }
        if(maxLength>0){
            labelWidth=(maxLength-4)*10+80;
        }
        //搜索，分为两列
        List<ModelFieldData> searchableAttrs=model.getAllModelSearchableAttr();
        len=searchableAttrs.size()/2+searchableAttrs.size()%2;        
        int searchHeight=baseHeight+len*16*2;
        List<ModelFieldData> leftSearchableAttrs=new ArrayList<>();
        for(int i=0;i<len;i++){
            leftSearchableAttrs.add(searchableAttrs.get(i));
        }
        List<ModelFieldData> rightSearchableAttrs=new ArrayList<>();
        for(int i=len;i<searchableAttrs.size();i++){
            rightSearchableAttrs.add(searchableAttrs.get(i));
        }
        
        if(modulePath.startsWith("/")){
            modulePath=modulePath.substring(1);
        }
        if(modulePath.endsWith("/")){
            modulePath=modulePath.substring(0,modulePath.length()-1);
        }
        if(createHeight>999){
            createHeight=999;
        }
        Integer createWidth=(createHeight*16)/9;
        if(createWidth<800){
            createWidth=800;
        }
        if(searchHeight>999){
            searchHeight=999;
        }
        Integer searchWidth=(searchHeight*16)/9;
        if(searchWidth<800){
            searchWidth=800;
        }
        context.put("namespace", modulePath);
        context.put("action", dealWithAcdtion(module));
        context.put("attrs", attrs);
        context.put("labelWidth", labelWidth);
        context.put("createWidth", createWidth.toString());
        context.put("createHeight", createHeight);
        context.put("searchWidth", searchWidth.toString());
        context.put("searchHeight", searchHeight);
        context.put("leftModelAttrs", leftModelAttrs);
        context.put("rightModelAttrs", rightModelAttrs);
        context.put("searchableAttrs", searchableAttrs);
        context.put("leftSearchableAttrs", leftSearchableAttrs);
        context.put("rightSearchableAttrs", rightSearchableAttrs);
        context.put("model", model.getClass().getSimpleName());
        context.put("title", model.getMetaData());
        context.put("icon", module);
        

        try {
            Template template = freemarkerConfiguration.getTemplate(templateName, ENCODING);
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, context);
            log.info("生成JS成功");
            return content;
        } catch (IOException | TemplateException e) {
            log.error("生成JS错误",e);
        }
        log.info("生成JS失败");
        return "";
    }
    /**
     * 将叶子模块名称转换为合适的访问struts2的名称
     * @param str 叶子模块名称
     * @return 
     */
    private static String dealWithAcdtion(String str){
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