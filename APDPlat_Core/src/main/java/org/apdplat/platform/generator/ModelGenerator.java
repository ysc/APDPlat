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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

/**
 *生成模型文件
 * @author 杨尚川
 */
public class ModelGenerator extends Generator {
    private final static ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    private static Configuration freemarkerConfiguration = null;

    static {
        factory.setTemplateLoaderPath(PropertyHolder.getProperty("model.generator.freemarker.template"));
        try {
            freemarkerConfiguration = factory.createConfiguration();
        } catch (IOException | TemplateException e) {
            LOG.error("初始化模板错误",e);
        }
    }
    /**
     * 一个目标模块可以有多个模型描述文件EXCEL
     * 根据目标模块的项目名称来获取模型描述文件
     * @param module 目标模块的项目名称
     * @return 模型描述文件的输入流列表
     */
    private static List<InputStream> getModelExcels(String module){     
        List<InputStream> ins=new ArrayList<>();
        try{
            String pattern="classpath*:generator/"+module+"/*.xls";
            LOG.info("模式："+pattern);
            Resource[] rs= resourcePatternResolver.getResources(pattern);
            LOG.info("扫描到的数量为："+rs.length);

            for(Resource r : rs){
                try {
                    InputStream in=r.getInputStream();
                    LOG.info("文件："+r.getFile().getAbsolutePath());
                    ins.add(in);
                } catch (Exception e) {
                    LOG.error("生成MODEL错误",e);
                }
            }
        }catch(Exception e){
            LOG.error("生成MODEL错误",e);
        }
        return ins;
    }
    /**
     * 自动代码生成的关键点
     * 
     * 将自动代码生成的EXCEL描述转换为JAVA对象描述
     * 
     * @param moduleProjectName 双重意义，一是指从哪里获取自动代码生成的配置文件EXCEL，二是生成的文件要保存到哪里去
     * @return JAVA描述的模型
     */
    public static List<ModelInfo> generate(String moduleProjectName){
        List<ModelInfo> all = new ArrayList<>();
        List<InputStream> ins = getModelExcels(moduleProjectName);
        ins.forEach(in -> {
            List<ModelInfo> modelInfos = readModelInfos(in);
            generate(modelInfos, moduleProjectName);
            all.addAll(modelInfos);
        });
        return all;
    }
    /**
     * 将JAVA描述的一系列模型生成源代码
     * @param modelInfos JAVA描述的一系列模型
     * @param moduleProjectName 目标模块的项目名称
     */
    private static void generate(List<ModelInfo> modelInfos,String moduleProjectName){
        for (ModelInfo modelInfo : modelInfos) {
            generate(modelInfo, moduleProjectName);
            LOG.info("-----------------------------------------------------------------------------");
            LOG.info("包："+modelInfo.getModelPackage());
            LOG.info("模型中文名称："+modelInfo.getModelChinese());
            LOG.info("模型英文名称："+modelInfo.getModelEnglish());

            for (Attr attr : modelInfo.getAttrs()) {
                LOG.info("        " + attr.toString());
            }
            LOG.info("-----------------------------------------------------------------------------");
        }
    }
    /**
     * 将JAVA描述的模型生成源代码
     * @param modelInfo JAVA描述的模型
     * @param moduleProjectName 目标模块的项目名称
     */
    private static void generate(ModelInfo modelInfo,String moduleProjectName) {
        String workspaceModuleBasePath = ModelGenerator.class.getResource("/").getFile().replace("target/classes/", "")+ "../" + moduleProjectName + "/src/main/java/";
        
        String templateName = "model.ftl";

        LOG.info("开始生成Model");
        LOG.info("workspaceModuleBasePath：" + workspaceModuleBasePath);

        Map<String, Object> context = new HashMap<>();
        context.put("modelInfo", modelInfo);

        boolean result = false;
        try {
            Template template = freemarkerConfiguration.getTemplate(templateName, ENCODING);
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, context);
            String modelPath = modelInfo.getModelPackage().replace(".", "/");
            String modelName = modelInfo.getModelEnglish();
            result = saveFile(workspaceModuleBasePath, modelPath, modelName, content);
        } catch (IOException | TemplateException e) {
            LOG.error("生成MODEL错误",e);
        }
        if (result) {
            LOG.info("Model生成成功");
        } else {
            LOG.info("忽略生成Model");
        }
    }
    /**
     * 保存生成的文件
     * @param workspaceModuleBasePath 目标模块的项目完整路径
     * @param modelPath 模型的包路径
     * @param modelName 模型文件名
     * @param content 模型文件内容
     * @return 是否成功
     */
    private static boolean saveFile(String workspaceModuleBasePath, String modelPath, String modelName, String content) {
        if (workspaceModuleBasePath == null) {
            return false;
        }
        File file = new File(workspaceModuleBasePath);
        file = new File(file, modelPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(file, modelName + ".java");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                LOG.error("生成MODEL错误",e);
            }
        } else {
            LOG.info("源文件已经存在，请删除 " + file.getAbsolutePath() + " 后在执行命令");
            return false;
        }
        saveFile(file, content);
        return true;
    }
    /**
     * 模型描述JAVA表示
     */
    final public static class ModelInfo {
        //包名称
        private String modelPackage;
        //模型名称类名称
        private String modelEnglish;
        //模型含义
        private String modelChinese;
        //是否有Date字段
        private boolean hasDateTime;
        //是否有DicItem字段
        private boolean hasDicItem;
        private List<Attr> attrs = new ArrayList<>();

        public boolean isHasDateTime() {
            return hasDateTime;
        }

        public void setHasDateTime(boolean hasDateTime) {
            this.hasDateTime = hasDateTime;
        }

        public boolean isHasDicItem() {
            return hasDicItem;
        }

        public void setHasDicItem(boolean hasDicItem) {
            this.hasDicItem = hasDicItem;
        }

        public List<Attr> getAttrs() {
            return attrs;
        }

        public void addAttr(Attr attr) {
            this.attrs.add(attr);
        }

        public void removeAttr(Attr attr) {
            this.attrs.remove(attr);
        }

        public String getModelChinese() {
            return modelChinese;
        }

        public void setModelChinese(String modelChinese) {
            this.modelChinese = modelChinese;
        }

        public String getModelEnglish() {
            return modelEnglish;
        }

        public void setModelEnglish(String modelEnglish) {
            this.modelEnglish = modelEnglish;
        }

        public String getModelPackage() {
            return modelPackage;
        }

        public void setModelPackage(String modelPackage) {
            this.modelPackage = modelPackage;
        }
        
        public boolean isHasOneToMany() {
            for(Attr attr : attrs){
                if(MapType.validType("OneToMany").equals(attr.map)){
                    return true;
                }                    
            }
            return false;
        }

        public boolean isHasMap() {
            for(Attr attr : attrs){
                if(!MapType.validType("None").equals(attr.map)){
                    return true;
                } 
                //当类型为DicItem的时候设置映射为DicItem
                if(attr.type.equals(AttrType.validType("DicItem"))){
                    attr.map="DicItem";
                    return true;
                }
            }
            return false;
        }
    }
    /**
     * 模型属性、类字段
     */
    final public static class Attr {

        private String name;
        private String type;
        private int length;
        private String des;
        private boolean searchable = false;
        private String map = MapType.validType("None");
        private String attrRef = "None";
        private boolean renderIgnore = false;
        private String dic = DicType.validType("None");
        private String dicName;

        public String getDic() {
            return dic;
        }

        public void setDic(String dic) {
            this.dic = dic;
        }

        public String getDicName() {
            return dicName;
        }

        public void setDicName(String dicName) {
            this.dicName = dicName;
        }

        public boolean isRenderIgnore() {
            return renderIgnore;
        }

        public void setRenderIgnore(boolean renderIgnore) {
            this.renderIgnore = renderIgnore;
        }

        public boolean isSearchable() {
            return searchable;
        }

        public void setSearchable(boolean searchable) {
            this.searchable = searchable;
        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMap() {
            return map;
        }

        public void setMap(String map) {
            this.map = map;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public String getAttrRef() {
            return attrRef;
        }

        public void setAttrRef(String attrRef) {
            this.attrRef = attrRef;
        }

        @Override
        public String toString() {
            return "Attr{" + "name=" + name + ", type=" + type + ", length=" + length + ", des=" + des + ", searchable=" + searchable + ", map=" + map + ", attrRef=" + attrRef + ", renderIgnore=" + renderIgnore + ", dic=" + dic + ", dicName=" + dicName + '}';
        }
    }

    final public static class MapType {

        private static final Map<String, String> types = new HashMap<>();

        static {
            types.put("None", "None");
            types.put("ManyToOne", "ManyToOne");
            types.put("ManyToMany", "ManyToMany");
            types.put("OneToOne", "OneToOne");
            types.put("OneToMany", "OneToMany");
        }

        public static String validType(String typeName) {
            String type = types.get(typeName);
            if (type == null) {
                StringBuilder str = new StringBuilder();
                str.append("【 ");
                for (String key : types.keySet()) {
                    str.append(key).append(",");
                }
                str = str.deleteCharAt(str.length() - 1);
                str.append(" 】");
                throw new RuntimeException("映射类型【" + type + "】不正确，映射类型只能为：" + str.toString());
            }
            return type;
        }
    }

    final public static class DicType {

        private static final Map<String, String> types = new HashMap<>();

        static {
            types.put("None", "None");
            types.put("SimpleDic", "SimpleDic");
            types.put("TreeDic", "TreeDic");
        }

        public static String validType(String typeName) {
            String type = types.get(typeName);
            if (type == null) {
                StringBuilder str = new StringBuilder();
                str.append("【 ");
                for (String key : types.keySet()) {
                    str.append(key).append(",");
                }
                str = str.deleteCharAt(str.length() - 1);
                str.append(" 】");
                throw new RuntimeException("字典类型【" + type + "】不正确，字典类型只能为：" + str.toString());
            }
            return type;
        }
    }

    final public static class AttrType {

        private static final Map<String, String> types = new HashMap<>();

        static {
            types.put("String", "String");
            types.put("Integer", "Integer");
            types.put("Float", "Float");
            types.put("DicItem", "DicItem");
            types.put("Date", "Date");
            types.put("Time", "Time");
        }

        public static String validType(String typeName) {
            String type = types.get(typeName);
            if (type == null) {
                types.put(typeName, typeName);
                return typeName;
            }
            return type;
        }
    }

    /**
     * 解析模型描述文件EXCEL
     * 解析EXCEL文件为JAVA对象
     * @param inputStream 模型描述文件EXCEL
     * @return 一系列JAVA对象
     */
    private static List<ModelInfo> readModelInfos(InputStream inputStream) {
        List<ModelInfo> models = new ArrayList<>();
        try {
            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                HSSFSheet sheet = workbook.getSheetAt(i);
                try {
                    HSSFRow row = sheet.getRow(2);
                    if (row == null) {
                        LOG.info("发现不合法的工作表：" + sheet.getSheetName());
                        continue;
                    }
                    HSSFCell cell = row.getCell(1);
                    //包名
                    String modelPackage = cell.getStringCellValue();
                    row = sheet.getRow(3);
                    cell = row.getCell(1);
                    //模型名称（英文）
                    String modelEnglish = cell.getStringCellValue();
                    row = sheet.getRow(4);
                    cell = row.getCell(1);
                    //模型名称（中文）
                    String modelChinese = cell.getStringCellValue();

                    ModelInfo modelInfo = new ModelInfo();
                    modelInfo.setModelPackage(modelPackage);
                    modelInfo.setModelEnglish(modelEnglish);
                    modelInfo.setModelChinese(modelChinese);

                    int rows = sheet.getPhysicalNumberOfRows();
                    //从第8行开始解析字段信息，
                    for (int rowNumber = 7; rowNumber < rows; rowNumber++) {
                        HSSFRow oneRow = sheet.getRow(rowNumber);

                        if (oneRow == null) {
                            continue;
                        }
                        Attr attr = new Attr();
                        //字段中文名称
                        HSSFCell oneCell = oneRow.getCell(0);
                        if (oneCell != null) {
                            String cellValue = oneCell.getStringCellValue();
                            if (cellValue != null && !"".equals(cellValue.trim()) && !"null".equals(cellValue.trim().toLowerCase())) {
                                attr.setDes(cellValue);
                            } else {
                                continue;
                            }
                        }
                        //字段英文名称
                        oneCell = oneRow.getCell(1);
                        if (oneCell != null) {
                            String cellValue = oneCell.getStringCellValue();
                            if (cellValue != null && !"".equals(cellValue.trim()) && !"null".equals(cellValue.trim().toLowerCase())) {
                                attr.setName(cellValue);
                            } else {
                                continue;
                            }
                        }
                        //字段类型
                        oneCell = oneRow.getCell(2);
                        if (oneCell != null) {
                            String cellValue = oneCell.getStringCellValue();
                            if (cellValue != null && !"".equals(cellValue.trim()) && !"null".equals(cellValue.trim().toLowerCase())) {
                                attr.setType(AttrType.validType(cellValue));
                            } else {
                                attr.setType(AttrType.validType("String"));
                            }
                        }
                        //字段长度（只针对string类型）
                        oneCell = oneRow.getCell(3);
                        if (oneCell != null) {
                            if(oneCell.getCellType() == HSSFCell.CELL_TYPE_STRING){
                                String cellValue = oneCell.getStringCellValue();
                                if (cellValue != null && !"".equals(cellValue.trim()) && !"null".equals(cellValue.trim().toLowerCase())) {
                                    try{
                                        int length=Integer.parseInt(cellValue);
                                        attr.setLength(length);
                                    }catch(Exception e){
                                        LOG.error("字段长度不是数值："+cellValue);
                                    }
                                }
                            }
                            if(oneCell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
                                double length = oneCell.getNumericCellValue();
                                attr.setLength((int)length);
                            }                            
                        }
                        //是否为搜索字段
                        oneCell = oneRow.getCell(4);
                        if (oneCell != null) {
                            boolean cellValue = oneCell.getBooleanCellValue();
                            attr.setSearchable(cellValue);
                        }
                        //是否忽略渲染到页面表格
                        oneCell = oneRow.getCell(5);
                        if (oneCell != null) {
                            boolean cellValue = oneCell.getBooleanCellValue();
                            attr.setRenderIgnore(cellValue);
                        }
                        //字段的下拉菜单类型
                        oneCell = oneRow.getCell(6);
                        if (oneCell != null) {
                            String cellValue = oneCell.getStringCellValue();
                            if (cellValue != null && !"".equals(cellValue.trim()) && !"null".equals(cellValue.trim().toLowerCase())) {
                                attr.setDic(DicType.validType(cellValue));
                            }
                        }
                        //下拉菜单对应的数据字典
                        oneCell = oneRow.getCell(7);
                        if (oneCell != null) {
                            String cellValue = oneCell.getStringCellValue();
                            if (cellValue != null && !"".equals(cellValue.trim()) && !"null".equals(cellValue.trim().toLowerCase())) {
                                attr.setDicName(cellValue);
                            }
                        }
                        //字段的映射类型
                        oneCell = oneRow.getCell(8);
                        if (oneCell != null) {
                            String cellValue = oneCell.getStringCellValue();
                            if (cellValue != null && !"".equals(cellValue.trim()) && !"null".equals(cellValue.trim().toLowerCase())) {
                                attr.setMap(MapType.validType(cellValue));
                            }
                        }
                        //映射对象渲染字段
                        oneCell = oneRow.getCell(9);
                        if (oneCell != null) {
                            String cellValue = oneCell.getStringCellValue();
                            if (cellValue != null && !"".equals(cellValue.trim()) && !"null".equals(cellValue.trim().toLowerCase())) {
                                attr.setAttrRef(cellValue);
                            }
                        }
                        if("Date".equals(attr.getType()) || "Time".equals(attr.getType())){
                            modelInfo.setHasDateTime(true);
                        }
                        if("DicItem".equals(attr.getType())){
                            modelInfo.setHasDicItem(true);
                            //如果指定了类型为DicItem，则默认的dic为SimpleDic
                            if(!"SimpleDic".equals(attr.getDic()) && !"TreeDic".equals(attr.getDic())){
                                attr.setDic("SimpleDic");
                            }
                            if(attr.getDicName() == null || "".equals(attr.getDicName())){
                                attr.setDicName(attr.getName());
                            }
                        }
                        modelInfo.addAttr(attr);
                    }
                    models.add(modelInfo);
                } catch (Exception e) {
                    LOG.error("解析工作表:" + sheet.getSheetName() + " 失败",e);
                }
            }
        } catch (IOException e) {
            LOG.error("生成MODEL错误",e);
        }
        return models;
    }
}