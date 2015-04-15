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

package org.apdplat.platform.generator;

import org.apdplat.module.module.model.Command;
import org.apdplat.module.module.model.Module;
import org.apdplat.module.module.service.ModuleParser;
import org.apdplat.module.module.service.ModuleService;
import org.apdplat.module.system.service.PropertyHolder;
import org.apdplat.platform.generator.ModelGenerator.ModelInfo;
import org.apdplat.platform.model.Model;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

/**
 *
 * @author 杨尚川
 */
public class ActionGenerator extends Generator{
    private static Configuration freemarkerConfiguration = null;

    static {
        factory.setTemplateLoaderPath(PropertyHolder.getProperty("action.generator.freemarker.template"));
        try {
            freemarkerConfiguration = factory.createConfiguration();
        } catch (IOException | TemplateException e) {
            LOG.error("初始化模板错误",e);
        }
    }
    /**
     * 此方法专门给model的main方法使用
     * @param clazz 
     */
    public static void generate(Class clazz) {        
        String workspaceModuleBasePath=ActionGenerator.class.getResource("/").getFile().replace("target/classes/", "")+"src/main/java/";
        generateFromModel(clazz,workspaceModuleBasePath);
    }
    /**
     * 生成批量模型对应的Action
     * @param modelInfos 批量模型
     * @param workspaceModuleBasePath 模块所在项目物理根路径
     */
    public static void generate(List<ModelInfo>  modelInfos,String workspaceModuleBasePath){
        modelInfos.forEach(modelInfo -> {
            String modelClzz=modelInfo.getModelPackage()+"."+modelInfo.getModelEnglish();
            Class clazz;
            try {
                clazz = Class.forName(modelClzz);
                generateFromModel(clazz,workspaceModuleBasePath+"/src/main/java/");
            } catch (ClassNotFoundException ex) {
                System.out.println("没有找到模型类: "+modelClzz);
            }
        });
        generateFromModule(modelInfos,workspaceModuleBasePath);
    }
    /**
     * 根据模块生成Action
     * @param workspaceModuleBasePath 开发时模块的根路径
     */
    private static void generateFromModule(List<ModelInfo>  modelInfos,String workspaceModuleBasePath) {
        List<String> models=new ArrayList<>();
        for(ModelInfo info : modelInfos){            
            models.add(info.getModelEnglish());
        }
        try {
            String moduleFile=workspaceModuleBasePath.replace("/src/main/java/", "/src/main/resources/META-INF/services/module.xml");
            
            //获取所有叶子模块
            Module rootModule = ModuleParser.getRootModule(new FileInputStream(moduleFile));
            List<Module> modules=ModuleService.getLeafModule(rootModule);
            //计算基本包名
            StringBuilder actionPackageBase = new StringBuilder(findPackageName(workspaceModuleBasePath));
            int index=actionPackageBase.indexOf("\\src\\main\\java\\");
            String newActionPackageBase=actionPackageBase.substring(index).replace("\\src\\main\\java\\", "").replace("\\", ".");
            actionPackageBase.setLength(0);
            actionPackageBase.append(newActionPackageBase);
            modules.forEach(module -> {
                List<Command> specialCommands=ModuleService.getSpecialCommand(module);    
                String action=Character.toUpperCase(module.getEnglish().charAt(0))+module.getEnglish().substring(1);
                String actionName=action+"Action";    
                String actionNamespace=ModuleService.getModulePath(module.getParentModule());
                String actionPackage=actionPackageBase+"."+actionNamespace.replace("/", ".").replace("\\", ".")+"action";
                actionNamespace=actionNamespace.substring(0, actionNamespace.length()-1);
                String actionPath=actionPackage.replace(".", "/");
                //判断此模块是否已被指定操作特定的模型
                Model model=actionToModel.get(module.getEnglish());
                System.out.println("actionToModel action: "+module.getEnglish());
                if(model!=null){
                    System.out.println("actionToModel model: "+model.getClass().getName());
                    System.out.println("模块 "+module.getChinese()+" 已被指定操作特定的模型 "+model.getMetaData());
                    //模块有指定的Model
                    String modelPackage=model.getClass().getName().replace("."+model.getClass().getSimpleName(), "");
                    generateAction(actionPackage,actionNamespace,actionName,modelPackage,model.getClass().getSimpleName(),workspaceModuleBasePath,specialCommands);
                    return;
                }
                System.out.println("模块 "+module.getChinese()+" 没有对应的模型 ");
                
                if(models.contains(action)){
                    System.out.println(module.getEnglish()+" 有对应的模型，忽略generateFromModule");
                    return;
                }
                
                System.out.println("generateFromModule actionPackage："+actionPackage);
                System.out.println("generateFromModule namespace："+actionNamespace);
                System.out.println("generateFromModule actionPath："+actionPath);
                System.out.println("generateFromModule action："+actionName);
                generateFromModule(specialCommands,actionPackage,actionNamespace,actionName,workspaceModuleBasePath,actionPath);
            });
        } catch (FileNotFoundException e) {
            LOG.error("生成ACTION错误",e);
        }
    }
    private static void generateFromModule(List<Command> specialCommands, String actionPackage, String actionNamespace, String actionName, String workspaceModuleBasePath, String actionPath) {
        String templateName="action_special.ftl";

        LOG.info("开始生成Action");
        LOG.info("workspaceModuleBasePath：" + workspaceModuleBasePath);
        //准备数据        
        Map<String, Object> context = new HashMap<>();
        context.put("actionPackage", actionPackage);
        context.put("actionNamespace", actionNamespace);
        context.put("actionName", actionName);
        context.put("specialCommands", specialCommands);

        boolean result=false;
        try {
            Template template = freemarkerConfiguration.getTemplate(templateName, ENCODING);
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, context);
            result=saveFile(workspaceModuleBasePath, actionPath, actionName, content);
        } catch (IOException | TemplateException e) {
            LOG.error("生成ACTION错误",e);
        }
        if(result){
            LOG.info("Action生成成功");
        }else{
            LOG.info("忽略生成Action");
        }
    }
    private static String findPackageName(String workspaceModuleBasePath){
        File base=new File(workspaceModuleBasePath);
        File model=findModel(base);
        if(model!=null){
            return model.getParentFile().getParent();
        }
        return null;
    }
    /**
     * 找到model文件夹
     * @param file
     * @return 
     */
    private static File findModel(File file){
        //查看当前文件夹是否为 model 文件夹
        if(file.getName().equals("model")){
            return file;
        }else{
            File[] subFiles=file.listFiles(new FilenameFilter(){

                @Override
                public boolean accept(File dir, String name) {
                    if(name.startsWith(".")){
                        return false;
                    }
                    return true;
                }
                
            });
            if(subFiles==null){
                return null;
            }
            //对当前文件夹的子文件夹进行判断
            for(File child : subFiles){
               if(child.getName().equals("model")){
                    return child;
                } 
            }
            //当前文件夹的子文件夹没有model文件夹
            for(File child : subFiles){
                File result=findModel(child);
                if(result!=null){
                    return result;
                }
            }            
        }
        return null;
    }
    /**
     * 根据模型生成Action
     * @param clazz 模型
     * @param workspaceModuleBasePath 开发时模块的根路径
     */
    private static void generateFromModel(Class clazz,String workspaceModuleBasePath) {
        String packageName=clazz.getPackage().getName();
        String p=packageName.replace(".model","");
        int index=p.lastIndexOf(".");
        String _package=p.substring(0,index);
        //最低层命名空间
        String actionNamespace=p.substring(index+1);
        String model=clazz.getSimpleName();
        String actionName=model+"Action";
        String actionPackage=_package+"."+actionNamespace+".action";
        String modelPackage=_package+"."+actionNamespace+".model";
        //完整命名空间
        //获取namespace
        int indexOf = packageName.indexOf(".module.");
        actionNamespace = packageName.substring(indexOf).replace(".module.", "").replace(".model", "").replace(".", "/");
        
        //普通情况下一个模型对应一个Action，如果某些模型不需要Action（即没有在module.xml中进行声明），则忽略生成此模型对应的Action
        //从module.xml中查找该model是否配置在模块文件中
        String shortModel=Character.toLowerCase(model.charAt(0))+model.substring(1);
        Module m=ModuleService.getModuleFromXml(shortModel);
        if(m==null){
            LOG.info(shortModel+" 没有在module.xml中进行声明，忽略生成Action");
            return ;
        }        
        //添加自定义的方法
        List<Command> specialCommands=ModuleService.getSpecialCommand(m);
        System.out.println("generateFromModel actionPackage："+actionPackage);
        System.out.println("generateFromModel actionNamespace："+actionNamespace);
        System.out.println("generateFromModel model："+model);
        
        generateAction(actionPackage,actionNamespace,actionName,modelPackage,model,workspaceModuleBasePath,specialCommands);
    }
    /**
     * 根据模型生成Action
     * @param workspaceModuleBasePath 开发时模块的根路径
     */
    private static void generateAction(String actionPackage, String actionNamespace, String actionName, String modelPackage,String model, String workspaceModuleBasePath, List<Command> specialCommands) {
        //检查参数，防止空指针
        if(specialCommands==null){
            specialCommands=new ArrayList<>();
        }
        System.out.println("generateAction actionPackage："+actionPackage);
        System.out.println("generateAction actionNamespace："+actionNamespace);
        System.out.println("generateAction actionName："+actionName);
        System.out.println("generateAction model："+model);
        System.out.println("generateAction workspaceModuleBasePath：" + workspaceModuleBasePath);
        
        String templateName="action.ftl";

        System.out.println("开始生成Action");
        //准备数据
        String actionPath=actionPackage.replace(".", "/"); 
        
        Map<String, Object> context = new HashMap<>();
        context.put("actionPackage", actionPackage);
        context.put("actionNamespace", actionNamespace);
        context.put("modelPackage", modelPackage);
        context.put("model", model);
        context.put("actionName", actionName);
        context.put("specialCommands", specialCommands);

        boolean result=false;
        try {
            Template template = freemarkerConfiguration.getTemplate(templateName, ENCODING);
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, context);
            result=saveFile(workspaceModuleBasePath, actionPath, actionName, content);
        } catch (IOException | TemplateException e) {
            LOG.error("生成ACTION错误",e);
        }
        if(result){
            System.out.println("Action生成成功");
        }else{
            System.out.println("忽略生成Action");
        }
    }
    /**
     * 
     * @param workspaceModuleBasePath
     * @param actionPath
     * @param actionName
     * @param content
     * @return 
     */
    private static boolean saveFile(String workspaceModuleBasePath, String actionPath, String actionName, String content) {
            if(workspaceModuleBasePath==null){
                return false;
            }
            File file = new File(workspaceModuleBasePath);
            file = new File(file, actionPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            file = new File(file, actionName+".java");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    LOG.error("生成ACTION错误",e);
                }
            }else{
                LOG.info("源文件已经存在，请删除 "+file.getAbsolutePath()+" 后在执行命令");
                return false;
            }
            saveFile(file,content);
            return true;
    }
}