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

import org.apdplat.module.monitor.model.MemoryState;
import org.apdplat.module.monitor.model.ProcessTime;
import org.apdplat.platform.log.APDPlatLogger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.apdplat.platform.log.APDPlatLoggerFactory;

/**
 *
 * @author 杨尚川
 */
public class MemoryStateChartDataService {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(MemoryStateChartDataService.class);

    public static List<MemoryState> getSequenceDataHH(List<MemoryState> models){    
        return getSequenceTimeData(models,"yyyy-MM-dd HH");
    }
    public static List<MemoryState> getSequenceDataDD(List<MemoryState> models){    
        return getSequenceTimeData(models,"yyyy-MM-dd");
    }
    public static List<MemoryState> getSequenceDataMonth(List<MemoryState> models){    
        return getSequenceTimeData(models,"yyyy-MM");
    }
    private static List<MemoryState> getSequenceTimeData(List<MemoryState> models,String format){        
        LinkedHashMap<String,MemoryState> data=new LinkedHashMap<>();
        //将日志数据转换为统计报表数据
        models.forEach(item -> {
            String key=new SimpleDateFormat(format).format(item.getRecordTime());
            MemoryState value=data.get(key);
            if(value==null){
                value=item;
            }else{
                //几次采集的内存数据，根据 已分配内存 来判断，谁大说明谁的内存使用情况最糟糕
                value=value.getTotalMemory()>item.getTotalMemory()?value:item;
            }

            data.put(key,value);
        });
        return new ArrayList<>(data.values());
    }

    /**
     * 同一命令只留最耗时的命令
     * @param models
     * @return 
     */
    private static List<ProcessTime> mini(List<ProcessTime> models) {
        LinkedHashMap<String,ProcessTime> data=new LinkedHashMap<>();
        models.forEach(item -> {
            ProcessTime value=data.get(item.getResource());
            if(value==null){
                value=item;
            }else{
                value=value.getProcessTime()>item.getProcessTime()?value:item;
            }
            data.put(item.getResource(), value);
        });
        return new ArrayList<>(data.values());
    }
}