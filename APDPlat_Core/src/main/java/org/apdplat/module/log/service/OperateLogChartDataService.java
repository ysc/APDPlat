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

package org.apdplat.module.log.service;

import org.apdplat.module.log.model.OperateLog;
import org.apdplat.module.log.model.OperateLogType;
import org.apdplat.module.log.model.OperateStatistics;
import org.apdplat.module.security.model.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 杨尚川
 */
public class OperateLogChartDataService {
    public static List<OperateStatistics> getData(List<OperateLog> models){
        Map<String,OperateStatistics> temp=new HashMap<>();
        //将日志数据转换为统计报表数据
        models.forEach(item -> {
            String username=item.getUsername();
            if(username == null){
                username = "匿名用户";
            }            
            OperateStatistics sta=temp.get(username);
            if(sta==null){
                sta=new OperateStatistics();
                sta.setUsername(username);
                temp.put(username, sta);
            }
            if(OperateLogType.ADD.equals(item.getOperatingType())){
                sta.increaseAddCount();
            }
            if(OperateLogType.DELETE.equals(item.getOperatingType())){
                sta.increaseDeleteCount();
            }
            if(OperateLogType.UPDATE.equals(item.getOperatingType())){
                sta.increaseUpdateCount();
            }
        });
        List<OperateStatistics> data=new ArrayList<>();
        temp.values().forEach(item -> {
            data.add(item);
        });
        return data;
    }
}