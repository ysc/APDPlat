package com.apdplat.module.dictionary.generator;

import com.apdplat.module.dictionary.model.Dic;
import com.apdplat.module.dictionary.service.DicParser;
import com.apdplat.module.system.service.PropertyHolder;
import com.apdplat.module.system.service.SystemListener;
import com.apdplat.platform.generator.Generator;
import com.apdplat.platform.util.FileUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.File;

/**
 *
 * @author ysc
 */
public class DictionaryGenerator extends Generator{
    private static Configuration freemarkerConfiguration = null;

    static {
        factory.setTemplateLoaderPath(PropertyHolder.getProperty("dictionary.generator.freemarker.template"));
        try {
            freemarkerConfiguration = factory.createConfiguration();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }

    public static void generateDic(String workspaceWebBasePath) {
        log.info("开始生成数据字典JS代码");
        log.info("runtimingWebBasePath：" + FileUtils.getAbsolutePath("/"));
        //准备数据
        Map<String, Object> context = new HashMap<String, Object>();
        List<Dic> dics=DicParser.getLeafDics();
        context.put("dics", dics);
        
        String templateName="dic.ftl";
        try {
            Template template = freemarkerConfiguration.getTemplate(templateName, ENCODING);
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, context);
            saveDicFile(workspaceWebBasePath, templateName, content);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (TemplateException ex) {
            ex.printStackTrace();
        }
        log.info("数据字典代码生成成功");
    }

    private static void saveDicFile(String workspaceWebBasePath, String templateName, String content) {
            if(workspaceWebBasePath==null){
                return;
            }
            File file = new File(workspaceWebBasePath);
            file = new File(file, "platform");
            file = new File(file, "js");
            if (!file.exists()) {
                file.mkdirs();
            }
            file = new File(file, templateName.replace("ftl", "js"));
            try {
                file.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            saveFile(file,content);
    }
}