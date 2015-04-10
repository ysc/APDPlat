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

import org.apdplat.module.monitor.model.IndexLog;
import org.apdplat.module.monitor.model.IndexLogResult;
import org.apdplat.platform.action.converter.DateTypeConverter;
import org.apdplat.platform.log.APDPlatLogger;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apdplat.platform.log.APDPlatLoggerFactory;

/**
 *
 * @author 杨尚川
 */
public class IndexLogChartDataService {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(IndexLogChartDataService.class);

    public static LinkedHashMap<String,Long> getSequenceData(List<IndexLog> models){    
        Collections.sort(models, new Comparator(){

            @Override
            public int compare(Object o1, Object o2) {
                IndexLog p1=(IndexLog)o1;
                IndexLog p2=(IndexLog)o2;
                return (int) (p1.getStartTime().getTime()-p2.getStartTime().getTime());
            }
        
        });
        LinkedHashMap<String,Long> data=new LinkedHashMap<>();
        if(models.size()<1){
            return data;
        }
        models.forEach(item -> {
            String key=DateTypeConverter.toDefaultDateTime(item.getStartTime());
            data.put(key, item.getProcessTime());
        });
        return data;
    }
    public static LinkedHashMap<String,Long> getRateData(List<IndexLog> models){    
        LinkedHashMap<String,Long> data=new LinkedHashMap<>();
        if(models.size()<1){
            return data;
        }
        AtomicLong success=new AtomicLong();
        AtomicLong fail=new AtomicLong();
        models.forEach(item -> {
            if(IndexLogResult.SUCCESS.equals(item.getOperatingResult())){
                success.incrementAndGet();
            }
            if(IndexLogResult.FAIL.equals(item.getOperatingResult())){
                fail.incrementAndGet();
            }
        });
        data.put("重建索引成功", success.get());
        data.put("重建索引失败", fail.get());
        return data;
    }
}