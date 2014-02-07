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

import org.apdplat.module.monitor.model.BackupLog;
import org.apdplat.module.monitor.model.BackupLogResult;
import org.apdplat.platform.action.converter.DateTypeConverter;
import org.apdplat.platform.log.APDPlatLogger;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import org.apdplat.platform.log.APDPlatLoggerFactory;

/**
 *
 * @author 杨尚川
 */
public class BackupLogChartDataService {    
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(BackupLogChartDataService.class);

    public static LinkedHashMap<String,Long> getSequenceData(List<BackupLog> models){    
        Collections.sort(models, new Comparator(){

            @Override
            public int compare(Object o1, Object o2) {
                BackupLog p1=(BackupLog)o1;
                BackupLog p2=(BackupLog)o2;
                return (int) (p1.getStartTime().getTime()-p2.getStartTime().getTime());
            }
        
        });
        LinkedHashMap<String,Long> data=new LinkedHashMap<>();
        if(models.size()<1){
            return data;
        }
        for(BackupLog item : models){
            String key=DateTypeConverter.toDefaultDateTime(item.getStartTime());
            data.put(key, item.getProcessTime());
        }
        return data;
    }
    public static LinkedHashMap<String,Long> getRateData(List<BackupLog> models){    
        LinkedHashMap<String,Long> data=new LinkedHashMap<>();
        if(models.size()<1){
            return data;
        }
        long success=0;
        long fail=0;
        for(BackupLog item : models){
            if(BackupLogResult.SUCCESS.equals(item.getOperatingResult())){
                success++;
            }
            if(BackupLogResult.FAIL.equals(item.getOperatingResult())){
                fail++;
            }
        }
        data.put("备份成功", success);
        data.put("备份失败", fail);
        return data;
    }
}