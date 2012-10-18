package com.apdplat.platform.generator;

import com.apdplat.module.system.service.PropertyHolder;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

/**
 *
 * @author ysc
 */
public class JspGenerator  extends Generator{
    private static Configuration freemarkerConfiguration = null;

    static {
        factory.setTemplateLoaderPath(PropertyHolder.getProperty("action.generator.freemarker.template"));
        try {
            freemarkerConfiguration = factory.createConfiguration();
        } catch (IOException | TemplateException e) {
            log.error("初始化模板错误",e);
        }
    }
    /**
     * 
     * @param modulePath 多级非叶子模块（路径名称等于模块英文名称）
     * @param module 叶子模块（路径名称等于模块英文名称）
     * @param title jsp页面标题
     */
    public static void generate(String modulePath, String module, String title) {
        String workspaceWebBasePath=JspGenerator.class.getResource("/").getFile().replace("target/classes/", "")+"src/main/webapp/";
        
        String templateName="jsp.ftl";

        log.info("开始生成Jsp");
        log.info("workspaceWebBasePath：" + workspaceWebBasePath);
        //准备数据
        int level=modulePath.split("/").length;
        String parentDir="";
        for(int i=0;i<level;i++){
            parentDir+="../";
        }
        
        Map<String, Object> context = new HashMap<>();
        context.put("title", title);
        context.put("parentDir", parentDir);
        context.put("js", module);
        
        boolean result=false;
        try {
            Template template = freemarkerConfiguration.getTemplate(templateName, ENCODING);
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, context);
            result=saveFile(workspaceWebBasePath, modulePath, module, content);
        } catch (IOException | TemplateException e) {
            log.error("生成JSP错误",e);
        }
        if(result){
            log.info("Jsp生成成功");
        }else{
            log.info("忽略生成Jsp");
        }
    }
    /**
     * 
     * @param workspaceWebBasePath  src\main\webapp
     * @param modulePath 多级非叶子模块（路径名称等于模块英文名称）
     * @param module 叶子模块（路径名称等于模块英文名称）
     * @param content 生成的jsp文件内容
     * @return 生成成功或失败
     */
    private static boolean saveFile(String workspaceWebBasePath, String modulePath, String module, String content) {
            if(workspaceWebBasePath==null){
                return false;
            }
            File jspDir = new File(workspaceWebBasePath);
            jspDir = new File(jspDir,"platform");
            jspDir = new File(jspDir, modulePath);
            if (!jspDir.exists()) {
                jspDir.mkdirs();
            }
            File jsDir=new File(jspDir,"js");
            if (!jsDir.exists()) {
                jsDir.mkdirs();
            }
            //生成JS文件
            File jsFile = new File(jsDir, module+".js");
            if (!jsFile.exists()) {
                try {
                    jsFile.createNewFile();
                    saveFile(jsFile,JsGenerator.getContent(modulePath,module));
                } catch (IOException e) {
                    log.error("生成JSP错误",e);
                }
            }
            //生成JSP文件
            File jspFile = new File(jspDir, module+".jsp");
            if (!jspFile.exists()) {
                try {
                    jspFile.createNewFile();
                    saveFile(jspFile,content);
                    return true;
                } catch (IOException e) {
                    log.error("生成JSP错误",e);
                }
            }else{
                log.info("源文件已经存在，请删除 "+jspFile.getAbsolutePath()+" 后再执行命令");                
            }
            return false;
    }
}
