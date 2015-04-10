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

package org.apdplat.module.monitor.service;

import java.util.ArrayList;
import org.apdplat.module.monitor.model.UserLogin;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author 杨尚川
 */
public class UserLoginChartDataService {
  
    public static LinkedHashMap<String,Long> getUserOnlineTime(List<UserLogin> models){
        models=getValidData(models);
        LinkedHashMap<String,Long> temp=new LinkedHashMap<>();
        //将日志数据转换为统计报表数据
        models.forEach(item -> {
            String username=item.getUsername();
            if(username == null){
                username = "匿名用户";
            }            
            Long value=temp.get(username);
            if(value==null){
                value=item.getOnlineTime();
            }else{
                value+=item.getOnlineTime();
            }
            temp.put(username, value);
        });
        return temp;
    }
    /**
     * 统计用户登录次数
     * @param models 用户登录日志
     * @return  以用户名为KEY，以登录次数为VALUE的MAP
     */
    public static LinkedHashMap<String,Long> getUserLoginTimes(List<UserLogin> models){
        LinkedHashMap<String,Long> temp=new LinkedHashMap<>();
        //将日志数据转换为统计报表数据
        models.forEach(item -> {
            String username=item.getUsername();
            if(username == null){
                username = "匿名用户";
            }
            
            Long value=temp.get(username);
            if(value==null){
                value=1l;
            }else{
                value++;
            }
            temp.put(username, value);
        });
        return temp;
    }
    public static List<UserLogin> getValidData(List<UserLogin> userLogins){
        List<UserLogin> models = new ArrayList<>();
        userLogins.forEach(userLogin -> {
            //如果登录时间或是注销时间有一项为空，则忽略
            if(userLogin.getLoginTime() != null && userLogin.getLogoutTime() != null){
                models.add(userLogin);
            }
        });
        return models;
    }
}