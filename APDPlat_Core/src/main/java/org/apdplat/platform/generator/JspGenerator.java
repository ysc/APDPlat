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

import org.apdplat.module.system.service.PropertyHolder;
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
 * @author 杨尚川
 */
public class JspGenerator  extends Generator{
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
     * 
     * @param modulePath 多级非叶子模块（路径名称等于模块英文名称）
     * @param module 叶子模块（路径名称等于模块英文名称）
     * @param title jsp页面标题
     */
    public static void generate(String modulePath, String module, String title) {
        String workspaceWebBasePath=JspGenerator.class.getResource("/").getFile().replace("target/classes/", "")+"src/main/webapp/";
        
        String templateName="jsp.ftl";

        LOG.info("开始生成Jsp");
        LOG.info("workspaceWebBasePath：" + workspaceWebBasePath);
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
            LOG.error("生成JSP错误",e);
        }
        if(result){
            LOG.info("Jsp生成成功");
        }else{
            LOG.info("忽略生成Jsp");
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
                    LOG.error("生成JSP错误",e);
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
                    LOG.error("生成JSP错误",e);
                }
            }else{
                LOG.info("源文件已经存在，请删除 "+jspFile.getAbsolutePath()+" 后再执行命令");                
            }
            return false;
    }
}