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

package org.apdplat.platform.criteria;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
/**
 * 属性编辑器
 * @author 杨尚川
 *
 */
public final class PropertyEditor {
    private Criteria criteria=Criteria.or;
    //此LIST里面的数据是OR的关系
    private List<PropertyEditor> subPropertyEditor=new ArrayList<>();
    private PropertyType propertyType;
    private Operator propertyOperator;
    private Property property = new Property();
    public static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    
    public PropertyEditor(){
    }
    public PropertyEditor(Criteria criteria){
        this.criteria=criteria;
    }
    public PropertyEditor(String name, String operator, String value) {
        setPropertyName(name);
        setPropertyOperator(operator);
        setPropertyValue(value);
    }
    public PropertyEditor(String name, Operator operator,String type, Object value) {
        setPropertyName(name);
        setPropertyOperator(operator);

        propertyType = Enum.valueOf(PropertyType.class, type);
        if(propertyType==PropertyType.String){
            property.setValue(value.toString());
        }else{
            property.setValue(value);
        }
    }
    public PropertyEditor(String name, Operator operator,PropertyType type, Object value) {
        setPropertyName(name);
        setPropertyOperator(operator);

        propertyType = type;
        if(propertyType==PropertyType.String){
            property.setValue(value.toString());
        }else{
            property.setValue(value);
        }
    }

    public PropertyEditor(String name, Operator operator, String value) {
        setPropertyName(name);
        setPropertyOperator(operator);
        setPropertyValue(value);
    }
    public PropertyEditor(String name, Operator operator, int value) {
        setPropertyName(name);
        setPropertyOperator(operator);
        property.setValue(value);
        propertyType = PropertyType.Integer;
    }

    public void setPropertyName(String name) {
        property.setName(name);
    }

    public void setPropertyOperator(String operator) {
        propertyOperator = Enum.valueOf(Operator.class, operator.toLowerCase());
    }

    public void setPropertyOperator(Operator operator) {
        propertyOperator = operator;
    }

    public void setPropertyValue(String value) {
        if(value==null){
            property.setValue(null);
        }else if (value.contains("-")) {
            try {
                property.setValue(format.parse(value));
                propertyType = PropertyType.Date;
            } catch (ParseException e) {
                property.setValue(value);
                propertyType = PropertyType.String;
            }
        } else if (value.contains(".")) {
            try {
                propertyType = PropertyType.Double;
                property.setValue(Double.parseDouble(value));
            } catch (NumberFormatException e) {
                property.setValue(value);
                propertyType = PropertyType.String;
            }
        } else if (value.contains("$")) {
            property.setValue(value.replace("$", ""));
            propertyType = PropertyType.String;
        } else if (value.contains("true") || value.contains("false")) {
            propertyType = PropertyType.Boolean;
            property.setValue(value.contains("true") ? Boolean.TRUE : Boolean.FALSE);
        } else {
            try {
                property.setValue(Integer.parseInt(value));
                propertyType = PropertyType.Integer;
            } catch (NumberFormatException e) {
                property.setValue(value);
                propertyType = PropertyType.String;
            }
        }
    }

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public Operator getPropertyOperator() {
        return propertyOperator;
    }

    public Property getProperty() {
        return property;
    }

    public List<PropertyEditor> getSubPropertyEditor() {
        return subPropertyEditor;
    }

    public void addSubPropertyEditor(PropertyEditor pe) {
        this.subPropertyEditor.add(pe);
    }

    public void removeSubPropertyEditor(PropertyEditor pe) {
        this.subPropertyEditor.remove(pe);
    }

    public Criteria getCriteria() {
        return criteria;
    }

    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }
}