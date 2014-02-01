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

package org.apdplat.platform.action;

import org.apdplat.module.security.model.User;
import org.apdplat.module.system.service.ExcelService;
import org.apdplat.platform.action.converter.DateTypeConverter;
import org.apdplat.platform.annotation.ModelAttr;
import org.apdplat.platform.annotation.ModelAttrRef;
import org.apdplat.platform.annotation.ModelCollRef;
import org.apdplat.platform.annotation.RenderIgnore;
import org.apdplat.platform.criteria.Property;
import org.apdplat.platform.model.Model;
import org.apdplat.platform.result.Page;
import org.apdplat.platform.util.ReflectionUtils;
import org.apdplat.platform.util.SpringContextUtils;
import org.apdplat.platform.util.Struts2Utils;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import org.apache.commons.lang.StringUtils;
import org.apdplat.platform.annotation.RenderDate;
import org.apdplat.platform.annotation.RenderTime;

/**
 *
 *控制器接口的抽象实现类
 *支持EXT JS
 * @author 杨尚川
 */
public abstract class ExtJSSimpleAction<T extends Model> extends ExtJSActionSupport implements Action {
    private boolean search=false;
    protected T model = null;
    protected Class<T> modelClass;
    protected Page<T> page = new Page<>();
    @Resource(name = "springContextUtils")
    protected SpringContextUtils springContextUtils;
    @Resource(name = "excelService")
    protected ExcelService excelService;
    protected Map map=null;

    @PostConstruct
    private void initModel() {
        try{
            if (this.model == null) {
                String modelName=getDefaultModelName();
                if("model".equals(modelName)){
                    this.model = (T)getRequest().getAttribute("model");
                }else{
                    this.model = (T) springContextUtils.getBean(modelName);
                }
                modelClass=(Class<T>)model.getClass();
            }
        }catch(Exception e){
            LOG.error("initModel fail");
        }
    }
    
    public String report(){
        
        return null;
    }

    public String chart(){
        if(StringUtils.isNotBlank(getQueryString())){
            //搜索出所有数据   
            beforeSearch();
            page=getService().search(getQueryString(), null, modelClass);
            List<T> models=processSearchResult(page.getModels());
            page.setModels(models);
        }else{
            beforeQuery();
            this.setPage(getService().query(modelClass));
        }
        //生成报表XML数据
        String data=generateReportData(page.getModels());
        if(StringUtils.isBlank(data)){
            LOG.info("生成的报表数据为空");
            return null;
        }
        Struts2Utils.renderXml(data);
        //业务处理完毕后删除页面数据引用，加速垃圾回收
        this.getPage().getModels().clear();
        this.setPage(null);
        
        return null;
    }

    protected String generateReportData(List<T> models){
        return null;
    }
    private String getDefaultModelName(){
        return getDefaultModelName(this.getClass());
    }

    @Override
    protected User refreshUser(User user){
        return getService().retrieve(User.class, user.getId());
    }
    @Override
    public String create() {
        try{
            try{
                checkModel(model);
            }catch(Exception e){
                map=new HashMap();
                map.put("success", false);
                map.put("message", e.getMessage()+",不能添加");
                Struts2Utils.renderJson(map);
                return null;
            }
            assemblyModelForCreate(model);
            objectReference(model);
            getService().create(model);
            afterSuccessCreateModel(model);
        }catch(Exception e){
            LOG.error("创建模型失败",e);
            afterFailCreateModel(model);

            map=new HashMap();
            map.put("success", false);
            map.put("message", "创建失败 "+e.getMessage());
            Struts2Utils.renderJson(map);
            return null;
        }
        map=new HashMap();
        map.put("success", true);
        map.put("message", "创建成功");
        Struts2Utils.renderJson(map);
        return null;
    }

    @Override
    public String createForm() {
        return FORM;
    }
    @Override
    public String retrieve() {
        this.setModel(getService().retrieve(modelClass, model.getId()));
        if(model==null){
            Struts2Utils.renderText("false");
            return null;
        }
        afterRetrieve(model);
        Map temp = new HashMap();
        renderJsonForRetrieve(temp);
        retrieveAfterRender(temp,model);
        Struts2Utils.renderJson(temp);

        return null;
    }
   
    protected void afterRetrieve(T model){
        
    }
    
    @Override
    public String updateForm() {
        setModel(getService().retrieve(modelClass, model.getId()));
        return null;
    }

    @Override
    public String updatePart() {
        try{
            Integer version=model.getVersion();
            //此时的model里面存的值是从浏览器传输过来的
            List<Property> properties=getPartProperties(model);
            //此时的model里面存的值是从数据库里面加载的
            model=getService().retrieve(modelClass,model.getId());
            
            //数据版本控制，防止多个用户同时修改一条数据，造成更新丢失问题
            if(version==null){
                LOG.info("前台界面没有传递版本信息");
                throw new RuntimeException("您的数据没有版本信息");
            }else{
                LOG.info("前台界面传递了版本信息,version="+version);
            }
            if(version!=model.getVersion()){
                LOG.info("当前数据的版本为 "+model.getVersion()+",您的版本为 "+version);
                throw new RuntimeException("您的数据已过期，请重新修改");
            }
            
            old(model);
            for(Property property : properties){
                //把从浏览器传来的值射入model
                if(property.getName().contains(".")){
                     //处理两个对象之间的引用，如：model.org.id=1
                     if(property.getName().contains(".id")){
                        String[] attr=property.getName().replace(".",",").split(",");
                        if(attr.length==2){
                            Field field=ReflectionUtils.getDeclaredField(model, attr[0]);
                            T change=getService().retrieve((Class<T>)field.getType(), (Integer)property.getValue());
                            ReflectionUtils.setFieldValue(model, attr[0], change);
                        }
                     }
                }
                else{
                    ReflectionUtils.setFieldValue(model, property.getName(), property.getValue());
                }
            }
            now(model);
            //在更新前调用模板方法对模型进行处理
            assemblyModelForUpdate(model);
            getService().update(model);
            afterSuccessPartUpdateModel(model);
        }catch(Exception e){
            LOG.error("更新模型失败",e);
            afterFailPartUpdateModel(model);
            map=new HashMap();
            map.put("success", false);
            map.put("message", "修改失败 "+e.getMessage());
            Struts2Utils.renderJson(map);
            return null;
        }
        map=new HashMap();
        map.put("success", true);
        map.put("message", "修改成功");
        Struts2Utils.renderJson(map);
        return null;
    }
    @Override
    public String updateWhole() {
        try{
            assemblyModelForUpdate(model);
            getService().update(model);
            afterSuccessWholeUpdateModel(model);
        }catch(Exception e){
            LOG.error("更新模型失败",e);
            afterFailWholeUpdateModel(model);
            Struts2Utils.renderText("false");
            return null;
        }
        Struts2Utils.renderText("true");
        return null;
    }
    public void prepareForDelete(Integer[] ids){

    }
    public String deleteSession() {
        return null;
    }
    @Override
    public String delete() {
        try{
            prepareForDelete(getIds());
            List<Integer> deletedIds=getService().delete(modelClass, getIds());
            afterDelete(deletedIds);
        }catch(Exception e){
            LOG.info("删除数据出错",e);
            Struts2Utils.renderText(e.getMessage());
            return null;
        }
        Struts2Utils.renderText("删除成功");
        return null;
    }
    public void afterDelete(List<Integer> deletedIds){

    }
    @Override
    public String query() {
        beforeQuery();
        if(search){
            search();
            return null;
        }
        this.setPage(getService().query(modelClass, getPageCriteria(), buildPropertyCriteria(), buildOrderCriteria()));
        Map json = new HashMap();
        json.put("totalProperty", page.getTotalRecords());
        List<Map> result = new ArrayList<>();
        renderJsonForQuery(result);
        json.put("root", result);
        Struts2Utils.renderJson(json);
        //业务处理完毕后删除页面数据引用，加速垃圾回收
        this.getPage().getModels().clear();
        this.setPage(null);
        
        return null;
    }

    public String export() {
        if(search){
            //导出全部搜索结果
            page=getService().search(getQueryString(), null, modelClass);
            List<T> models=processSearchResult(page.getModels());
            page.setModels(models);
            //导出当前页的搜索结果
            //this.setPage(getService().search(getQueryString(), getPageCriteria(), modelClass));
        }else{
            //导出全部数据
            this.setPage(getService().query(modelClass, null, buildPropertyCriteria(), buildOrderCriteria()));
            //导出当前页的数据
            //this.setPage(getService().query(modelClass, getPageCriteria(), buildPropertyCriteria(), buildOrderCriteria()));
        }
        List<List<String>> result = new ArrayList<>();
        renderForExport(result);
        String path=excelService.write(result, exportFileName());
        Struts2Utils.renderText(path);
        //业务处理完毕后删除页面数据引用，加速垃圾回收
        this.getPage().getModels().clear();
        this.setPage(null);
        
        return null;
    }
    private List<T> processSearchResult(List<T> models){
        List<T> result =  new ArrayList<>();
        for(T obj : models){
            T t=getService().retrieve(modelClass, obj.getId());
            if(t!=null){
                result.add(t);
            }
        }
        return result;
    }
    @Override
    public String search() {
        beforeSearch();
        page=getService().search(getQueryString(), getPageCriteria(), modelClass);
        //List<T> models=processSearchResult(page.getModels());
        //page.setModels(models);

        Map json = new HashMap();
        json.put("totalProperty", page.getTotalRecords());
        List<Map> result = new ArrayList<>();
        renderJsonForSearch(result);
        json.put("root", result);
        Struts2Utils.renderJson(json);
        return null;
    }
    protected  void beforeQuery(){

    }
    protected  void beforeSearch(){

    }
    protected void checkModel(T model) throws Exception{

    }
    /**
     * 在【添加】一个特定的【完整】的Model之前对Model的组装，以确保组装之后的Model是一个语义完整的模型
     * @return
     */
    protected void assemblyModelForCreate(T model) {

    }
    protected void assemblyModelForUpdate(T model) {

    }
    /**
     * 模型【部分】【更新成功】后的回调方法
     * @return
     */
    protected void afterSuccessPartUpdateModel(T model) {

    }
    protected void old(T model) {

    }
    protected void now(T model) {

    }
    /**
     * 模型【部分】【更新失败】后的回调方法
     * @return
     */
    protected void afterFailPartUpdateModel(T model) {

    }
    /**
     * 模型【完整】【更新成功】后的回调方法
     * @return
     */
    protected void afterSuccessWholeUpdateModel(T model) {

    }
    /**
     * 模型【完整】【更新失败】后的回调方法
     * @return
     */
    protected void afterFailWholeUpdateModel(T model) {

    }
    /**
     * 模型【创建成功】后的回调方法
     * @return
     */
    protected void afterSuccessCreateModel(T model) {

    }
    /**
     * 模型【创建失败】后的回调方法
     * @return
     */
    protected void afterFailCreateModel(T model) {

    }

    /////////////////////////////////////以下三个方法有默认实现，可以简化Action，如果有特殊需要，子类Action可以覆写
    /**
     * 渲染需要在页面【详细信息】中显示的字段
     * @return
     */
    protected void renderJsonForRetrieve(Map map) {
        render(map,model);
    }
    /**
     * 渲染需要在页面【搜索结果表格】表格中显示的字段
     * @return
     */
    protected void renderJsonForSearch(List result) {
        renderJsonForQuery(result);
    }
    /**
     * 渲染需要在页面【表格】中显示的字段
     * @return
     */
    protected void renderJsonForQuery(List result) {
        for (T obj : page.getModels()) {
            Map temp = new HashMap();
            render(temp,obj);
            afterRender(temp,obj);
            result.add(temp);
        }
    }
    /**
     * 自动生成和自定义相结合
     * @param map 自动渲染好的对象
     */
    protected void afterRender(Map map,T obj){
        
    }
    protected void retrieveAfterRender(Map map,T obj){
        
    }
    
    protected void render(Map map,T obj){
        //获取所有字段，包括继承的
        List<Field> fields = ReflectionUtils.getDeclaredFields(model);
        for (Field field : fields) {
            if(field.isAnnotationPresent(RenderIgnore.class)){
                continue;
            }
            addFieldValue(obj, field, map);
        }
    }
    /**
     * @param src
     * 源字符串
     * @return 字符串，将src的第一个字母转换为大写，src为空时返回null
     */
    public static String change(String src) {
        if (src != null) {
            StringBuilder sb = new StringBuilder(src);
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
            return sb.toString();
        } else {
            return null;
        }
    }

    protected String  exportFileName() {
        return model.getMetaData()+".xls";
    }

    protected void renderForExport(List<List<String>> result) {
        List<String> data = new ArrayList<>();
        //获取所有字段，包括继承的
        List<Field> fields = ReflectionUtils.getDeclaredFields(model);
        for (Field field : fields) {
            if(field.isAnnotationPresent(ModelAttr.class)){
                ModelAttr attr = field.getAnnotation(ModelAttr.class);
                String fieldAttr = attr.value();
                data.add(fieldAttr);
            }
        }
       result.add(data);
       for( T obj : page.getModels()){
           data =new ArrayList<>();
           renderDataForExport(data,obj);
           result.add(data);
       }
    }
    private void renderDataForExport(List<String> data, T obj) {
        //获取所有字段，包括继承的
        List<Field> fields = ReflectionUtils.getDeclaredFields(obj);

        for (Field field : fields) {
            if(field.isAnnotationPresent(ModelAttr.class)){
                //导出的时候，如果是复杂类型，则忽略*_id属性
                Map<String,String> temp=new HashMap<>();
                addFieldValue(obj,field,temp);
                //复杂类型对应两个值
                temp.remove(field.getName()+"_id");
                data.addAll(temp.values());
            }
        }
    }
    private void addFieldValue(T obj, Field field, List<String> data){
        Map<String,String> temp=new HashMap<>();
        addFieldValue(obj,field,temp);
        data.addAll(temp.values());
    }
    private void addFieldValue(T obj, Field field, Map<String,String> data){
        String fieldName = field.getName();
        try{
            if(field.isAnnotationPresent(Lob.class)){
                LOG.debug("字段["+fieldName+"]为大对象，忽略生成JSON字段");
                return;
            }
            Object value = ReflectionUtils.getFieldValue(obj, field);
            if(value==null){
                data.put(fieldName, "");
                return;
            }
            //处理集合类型
            if(field.isAnnotationPresent(ModelCollRef.class)){
                ModelCollRef ref = field.getAnnotation(ModelCollRef.class);
                String fieldRef = ref.value();
                Collection col=(Collection)value;
                String colStr="";
                if(col!=null){
                    LOG.debug("处理集合,字段为："+field.getName()+",大小为："+col.size());
                    if(col.size()>0){
                        StringBuilder str=new StringBuilder();
                        for(Object m : col){
                            str.append(ReflectionUtils.getFieldValue(m, fieldRef).toString()).append(",");
                        }
                        str=str.deleteCharAt(str.length()-1);
                        colStr=str.toString();
                    }
                }else{
                    LOG.debug("处理集合失败，"+value+" 不能转换为集合");
                }
                data.put(fieldName, colStr);
                return ;
            }
            //处理复杂对象类型
            if(field.isAnnotationPresent(ModelAttrRef.class)){
                LOG.debug("处理对象,字段为："+field.getName());
                ModelAttrRef ref = field.getAnnotation(ModelAttrRef.class);
                String fieldRef = ref.value();
                //加入复杂对象的ID
                Object id = ReflectionUtils.getFieldValue(value, "id");
                data.put(fieldName+"_id", id.toString());
                //因为是复杂对象，所以变换字段名称
                fieldName=fieldName+"_"+fieldRef;
                //获取fieldRef的值
                value = ReflectionUtils.getFieldValue(value, fieldRef);
            }
            if(value.getClass()==null){
                data.put(fieldName, "");
                return;
            }
            String valueClass=value.getClass().getSimpleName();

            if("PersistentBag".equals(valueClass)){
                value="";
            }
            if("Timestamp".equals(valueClass) || "Date".equals(valueClass)){
                if(field.isAnnotationPresent(RenderDate.class)){
                    value=DateTypeConverter.toDefaultDate((Date)value);
                }else if(field.isAnnotationPresent(RenderTime.class)){
                    value=DateTypeConverter.toDefaultDateTime((Date)value);
                }else{
                    //如果没有指定渲染类型，则根据@Temporal来判断
                    String temporal = "TIMESTAMP";
                    if(field.isAnnotationPresent(Temporal.class)){
                        temporal = field.getAnnotation(Temporal.class).value().name();
                    }
                    switch (temporal) {
                        case "TIMESTAMP":
                            value=DateTypeConverter.toDefaultDateTime((Date)value);
                            break;
                        case "DATE":
                            value=DateTypeConverter.toDefaultDate((Date)value);
                            break;
                    }
                }
            }
            //处理下拉菜单
            if("DicItem".equals(valueClass)){
                //当修改数据的时候，需要该值
                data.put(fieldName+"Id", ReflectionUtils.getFieldValue(value, "id").toString());
                
                value = ReflectionUtils.getFieldValue(value, "name");
            }
            data.put(fieldName, value.toString());
        }catch(Exception e){
            LOG.error("获取字段值失败",e);
        }
    }
    public T getModel() {
        return this.model;
    }

    public void setModel(T model) {
        this.model = model;
    }

    public Page<T> getPage() {
        return page;
    }

    public void setPage(Page<T> page) {
        this.page = page;
    }

    public boolean isSearch() {
        return search;
    }

    public void setSearch(boolean search) {
        this.search = search;
    }

    protected void objectReference(T model) {
        Field[] fields = model.getClass().getDeclaredFields();//获得对象方法集合
        for (Field field : fields) {// 遍历该数组
            if(field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(OneToOne.class)){
                LOG.debug(model.getMetaData()+" 有ManyToOne 或 OneToOne映射，字段为"+field.getName());
                Model value=(Model)ReflectionUtils.getFieldValue(model, field);
                if(value==null){
                    LOG.debug(model.getMetaData()+" 的字段"+field.getName()+"没有值，忽略处理");
                    continue;
                }
                int id=value.getId();
                LOG.debug("id: "+id);
                value=getService().retrieve(value.getClass(), id);
                ReflectionUtils.setFieldValue(model, field, value);
            }
        }
    }
}