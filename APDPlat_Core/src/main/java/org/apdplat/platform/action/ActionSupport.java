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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apdplat.module.security.model.User;
import org.apdplat.platform.common.DataPrivilegeControl;
import org.apdplat.platform.criteria.Operator;
import org.apdplat.platform.criteria.Order;
import org.apdplat.platform.criteria.OrderCriteria;
import org.apdplat.platform.criteria.PageCriteria;
import org.apdplat.platform.criteria.Property;
import org.apdplat.platform.criteria.PropertyCriteria;
import org.apdplat.platform.criteria.PropertyEditor;
import org.apdplat.platform.criteria.Sequence;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.model.Model;
import org.apdplat.platform.util.ReflectionUtils;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Resource;
import javax.persistence.MappedSuperclass;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.apdplat.platform.service.ServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@MappedSuperclass
public abstract class ActionSupport extends DataPrivilegeControl{
    protected final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(getClass());
    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private Feedback feedback;
    private PageCriteria pageCriteria = new PageCriteria(1, 17);
    private String propertyCriteria;
    private String orderCriteria;
    //客户端传过来的构造好的查询字符串
    protected String queryString;
    private String modelName;
    //三种获取批量ID的形式
    private Integer[] id;
    private String ids;
    private boolean allPage=false;

    protected static final OrderCriteria defaultOrderCriteria = new OrderCriteria();

    static {
        defaultOrderCriteria.addOrder(new Order("id", Sequence.DESC));
    }

    @Autowired
    protected ServletContext servletContext;
    
    @Resource(name = "serviceFacade")
    private ServiceFacade service;
    
    public String toJson(Object object){
        try{
            return OBJECT_MAPPER.writeValueAsString(object);
        }catch(Exception e){
            LOG.error("生成json出错", e);
        }
        return "";
    }
    
    /**
     * 子类可重载使用特定的数据库服务
     * @return 
     */
    public ServiceFacade getService(){
        return service;
    }
    
    public Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

    public String execute() {
        LOG.info("调用了action的默认execute方法");
        return null;
    }

    protected String getDefaultModelName(Class clazz){
        String modelClassName =  ReflectionUtils.getSuperClassGenricType(clazz).getSimpleName();
        return Character.toLowerCase(modelClassName.charAt(0))+modelClassName.substring(1);
    }

    public Integer[] getIds() {
        if (ids != null && ids.contains("-") && ids.contains(",")) {
            Set<Integer> result = new HashSet<>();
            String[] idInfo = ids.split(",");
            for (String info : idInfo) {
                if (info.contains("-")) {
                    String[] inner = info.split("-");
                    int start = Integer.parseInt(inner[0]);
                    int end = Integer.parseInt(inner[1]);
                    if (start > end) {
                        int temp = start;
                        start = end;
                        end = temp;
                    }
                    for (int i = start; i < end + 1; i++) {
                        result.add(i);
                    }
                } else {
                    result.add(Integer.parseInt(info));
                }
            }
            return result.toArray(new Integer[result.size()]);
        }
        if (ids != null && ids.contains("-")) {
            String[] idInfo = ids.split("-");
            int start = Integer.parseInt(idInfo[0]);
            int end = Integer.parseInt(idInfo[1]);
            if (start > end) {
                int temp = start;
                start = end;
                end = temp;
            }
            Set<Integer> result = new HashSet<>();
            for (int i = start; i < end + 1; i++) {
                result.add(i);
            }
            return result.toArray(new Integer[result.size()]);
        }
        if (ids != null && ids.contains(",")) {
            String[] idInfo = ids.split(",");
            Set<Integer> result = new HashSet<>();
            for (int i = 0; i < idInfo.length; i++) {
                result.add(Integer.parseInt(idInfo[i]));
            }
            return result.toArray(new Integer[result.size()]);
        }
        if (ids != null) {
            Integer[] result = new Integer[1];

            result[0] = Integer.parseInt(ids);

            return result;
        }

        return id;
    }

    //propertyCriteria =score:gt:30,score:lt:60,birthday:gt:1983-10-21,birthday:lt:2009-12-12
    public PropertyCriteria buildPropertyCriteria() {
        if (StringUtils.isBlank(propertyCriteria)) {
            return null;
        }
        PropertyCriteria result = new PropertyCriteria();
        propertyCriteria=propertyCriteria.replace("，", ",");
        propertyCriteria=propertyCriteria.replace(";", ",");
        propertyCriteria=propertyCriteria.replace("；", ",");
        //,号用来分割属性
        String[] properties = propertyCriteria.split(",");
        int start=0;
        //判断是否支持集合查询
        if(propertyCriteria.startsWith("collection:") && properties.length>2){
            String collection=properties[0].split(":")[1];
            String object=properties[1].split(":")[1];
            result.setCollection(collection);
            result.setObject(object);
            start=2;
        }
        for (int i=start;i<properties.length;i++) {
            String prop=properties[i];
            //:号用来分割属性内部的类型、属性名、操作符、属性值
            String[] propInfo = prop.split(":");
            if(propInfo.length!=3){
                LOG.error("属性过滤器错误："+prop);
                continue;
            }

            PropertyEditor propertyEditor = new PropertyEditor(propInfo[0], propInfo[1], propInfo[2]);

            result.addPropertyEditor(propertyEditor);
        }

        return result;
    }
    public OrderCriteria buildOrderCriteria() {
        if (orderCriteria == null) {
            return defaultOrderCriteria;
        }
        OrderCriteria result = new OrderCriteria();
        String[] orders = orderCriteria.split(",");
        for (String order : orders) {
            String[] orderInfo = order.split(":");
            result.addOrder(new Order(orderInfo[0], orderInfo[1]));
        }

        return result;
    }
    protected HttpServletRequest getRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs.getRequest();
    }
    protected HttpSession getSession() {
        return getRequest().getSession();
    }

    private Enumeration<?> getRequestParameterNames() {
        return getRequest().getParameterNames();
    }

    private String getRequestParameterValue(String par) {
        return getRequest().getParameter(par);
    }

    public boolean hasRequest() {
        if (getRequest() != null) {
            return true;
        }
        return false;
    }
    /**
     * 在更新一个特定的部分的Model之前对Model添加需要修改的属性
     * @return
     */
    protected void assemblyModelForPartUpdate(List<Property> properties) {

    }
    protected <T extends Model> List<Property> getPartProperties(T model) {
        List<Property> properties = new ArrayList<>();

        Enumeration<?> pars = getRequestParameterNames();
        while (pars.hasMoreElements()) {
            String par = (String) pars.nextElement();
            if (par.startsWith("model.") && !par.equals("model.id")) {
                String prop = par.replace("model.", "");
                if(prop.contains(".")){
                     if(prop.contains(".id")){
                         //处理两个对象之间的引用，如：model.org.id=1
                        String[] attr=prop.replace(".",",").split(",");
                        if(attr.length==2){
                            Object obj=ReflectionUtils.getFieldValue(model, attr[0]);
                            properties.add(new Property(prop, ReflectionUtils.getFieldValue(obj, attr[1])));
                        }
                     }
                }
                else{
                    properties.add(new Property(prop, ReflectionUtils.getFieldValue(model, prop)));
                }
            }
        }
        assemblyModelForPartUpdate(properties);

        return properties;
    }

    protected void buildModel(Model model) {
        Enumeration<?> pars = getRequestParameterNames();
        while (pars.hasMoreElements()) {
            String par = (String) pars.nextElement();
            if (par.startsWith("model.")) {
                String fieldName = par.replace("model.", "");
                String value = getRequestParameterValue(par);
                //这里应该有类型转换和合法性校验
                PropertyEditor propertyEditor = new PropertyEditor(fieldName, Operator.eq, value);
                Class<?> fieldType = ReflectionUtils.getDeclaredField(model, fieldName).getType();
                Object fieldValue;
                if (fieldType != propertyEditor.getPropertyType().getValue()) {
                    LOG.debug(fieldType + "!=" + propertyEditor.getPropertyType().getValue());
                    fieldValue = propertyEditor.getProperty().getValue().toString();
                } else {
                    fieldValue = propertyEditor.getProperty().getValue();
                }
                ReflectionUtils.setFieldValue(model, fieldName, fieldValue);
            }
        }
    }

    public void setId(Integer[] id) {
        this.id = id;
    }

    public String getQueryString() {
        StringBuilder result=new StringBuilder();
        if(queryString!=null && !queryString.trim().equals("")){
            String[] props=queryString.trim().split(" ");
            for(int i=0;i<props.length-1;i++){
                String prop=props[i];
                if("".equals(prop.trim())){
                    continue;
                }
                if(prop.contains("[")){
                    result.append(prop).append(" ");
                    continue;
                }
                if(prop.contains("-")){
                    result.append(prop).append(" ");
                    continue;
                }
                if(prop.contains(":")){
                    result.append(prop).append(" ");
                    continue;
                }
                if("TO".equals(prop.trim())){
                    result.append(prop).append(" ");
                    continue;
                }
                if(prop.contains("]")){
                    result.append(prop).append(" ");
                    continue;
                }
                if("AND".equals(prop.trim())){
                    result.append("AND").append(" ");
                    continue;
                }
                String[] term=prop.split(":");
                //处理关键词为空格的情况
                if(term.length!=2){
                    prop=prop.trim()+"*";
                    term=prop.split(":");
                }
                //如果关键词的长度为1,并且不为*,?以及数字，则在此字符之前和之后加入*
                if(term[1].trim().length()==1 && !term[1].trim().equals("*") && !term[1].trim().equals("?") && !Character.isDigit(term[1].trim().charAt(0))){
                    result.append(term[0])
                          .append(":")
                          .append("*")
                          .append(term[1])
                          .append("*")
                          .append(" ");
                }else{
                    result.append(prop).append(" ");
                }
            }
            result.append(props[props.length-1]);
        }
        String ret=result.toString().trim();
        while(ret.contains("AND AND")){
            ret=ret.replace("AND AND", "AND");
        }
        ret=getCustomQueryString(ret);
        /*
        User user=UserHolder.getCurrentLoginUser();
        int s=ret.lastIndexOf(":");
        String modelClass=ret.substring(s+1).trim();
        //如果用户不是超级用户就限制数据访问
        if(user!=null && !user.isSuperManager() && needPrivilege(modelClass)){
            user=refreshUser(user);
            Org org=user.getOrg();
            List<String> child=OrgService.getChildNames(org);
            StringBuilder str=new StringBuilder();
            str.append("(");
            //用户可以操纵自己的数据
            str.append(" +username:").append(user.getUsername());
            //如果用户的组织架构有子机构，则用户除了能操纵自己的数据，还能操纵子机构的所有数据
            if(!child.isEmpty()){
                for(String orgName : child){
                    str.append(" OR +orgName:").append(orgName);
                }
            }
            str.append(") AND ");
            ret=str.toString()+ret;
        }
         * 
         */
        
        return ret;
    }
    protected String getCustomQueryString(String queryString){
        return queryString;
    }
    protected User refreshUser(User user){
        return user;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public PageCriteria getPageCriteria() {
        if(this.isAllPage()){
            return null;
        }
        return pageCriteria;
    }

    public void setPageCriteria(PageCriteria pageCriteria) {
        this.pageCriteria = pageCriteria;
    }

    public void setPropertyCriteria(String propertyCriteria) {
        this.propertyCriteria = propertyCriteria;
    }

    public void setOrderCriteria(String orderCriteria) {
        this.orderCriteria = orderCriteria;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }

    public boolean isAllPage() {
        return allPage;
    }

    public void setAllPage(boolean allPage) {
        this.allPage = allPage;
    }
}