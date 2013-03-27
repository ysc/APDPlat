/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.apdplat.module.monitor.service;

import com.apdplat.module.monitor.model.UserLogin;
import com.apdplat.module.security.model.User;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author ysc
 */
public class UserLoginChartDataService {
  
    public static LinkedHashMap<String,Long> getUserOnlineTime(List<UserLogin> models){        
        LinkedHashMap<String,Long> temp=new LinkedHashMap<>();
        //将日志数据转换为统计报表数据
        for(UserLogin item : models){
            User user=item.getOwnerUser();
            String username="匿名用户";
            if(user!=null){
                username=user.getUsername();
            }
            Long value=temp.get(username);
            if(value==null){
                value=item.getOnlineTime();
            }else{
                value+=item.getOnlineTime();
            }
            temp.put(username, value);
        }
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
        for(UserLogin item : models){
            User user=item.getOwnerUser();
            String username="匿名用户";
            if(user!=null){
                username=user.getUsername();
            }
            Long value=temp.get(username);
            if(value==null){
                value=1l;
            }else{
                value++;
            }
            temp.put(username, value);
        }
        return temp;
    }  
}