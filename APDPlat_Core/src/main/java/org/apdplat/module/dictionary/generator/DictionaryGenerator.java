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

package org.apdplat.module.dictionary.generator;

import org.apdplat.module.dictionary.model.Dic;
import org.apdplat.module.dictionary.service.DicParser;
import org.apdplat.module.system.service.PropertyHolder;
import org.apdplat.platform.generator.Generator;
import org.apdplat.platform.util.FileUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

/**
 *
 * @author 杨尚川
 */
public class DictionaryGenerator extends Generator{
    private static Configuration freemarkerConfiguration = null;

    static {
        factory.setTemplateLoaderPath(PropertyHolder.getProperty("dictionary.generator.freemarker.template"));
        try {
            freemarkerConfiguration = factory.createConfiguration();
        } catch (IOException | TemplateException e) {
            LOG.error("生成数据字典出错",e);
        }
    }

    public static void generateDic(String workspaceWebBasePath) {
        LOG.info("开始生成数据字典JS代码");
        LOG.info("runtimingWebBasePath：" + FileUtils.getAbsolutePath("/"));
        //准备数据
        Map<String, Object> context = new HashMap<>();
        List<Dic> dics=DicParser.getLeafDics();
        context.put("dics", dics);
        
        String templateName="dic.ftl";
        try {
            Template template = freemarkerConfiguration.getTemplate(templateName, ENCODING);
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, context);
            saveDicFile(workspaceWebBasePath, templateName, content);
        } catch (IOException | TemplateException e) {
            LOG.error("生成数据字典出错",e);
        }
        LOG.info("数据字典代码生成成功");
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
            } catch (IOException e) {
                LOG.error("生成数据字典出错",e);
            }
            saveFile(file,content);
    }
}