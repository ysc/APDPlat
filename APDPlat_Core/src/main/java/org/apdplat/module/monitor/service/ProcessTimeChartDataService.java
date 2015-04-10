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

import org.apdplat.module.monitor.model.ProcessTime;
import org.apdplat.module.security.model.User;
import org.apdplat.platform.action.converter.DateTypeConverter;
import org.apdplat.platform.log.APDPlatLogger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apdplat.platform.log.APDPlatLoggerFactory;

/**
 *
 * @author 杨尚川
 */
public class ProcessTimeChartDataService {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(ProcessTimeChartDataService.class);
  

    public static LinkedHashMap<String, Long> getProcessRate(List<ProcessTime> models) {    
        Collections.sort(models, new Comparator(){

            @Override
            public int compare(Object o1, Object o2) {
                ProcessTime p1=(ProcessTime)o1;
                ProcessTime p2=(ProcessTime)o2;
                return (int) (p1.getStartTime().getTime()-p2.getStartTime().getTime());
            }
        
        });
        LinkedHashMap<String,Long> data=new LinkedHashMap<>();
        if(models.size()<1){
            return data;
        }
        ProcessTime first=models.get(0);
        ProcessTime latest=models.get(models.size()-1);
        LOG.debug("首次请求时间："+DateTypeConverter.toDefaultDateTime(first.getStartTime()));
        LOG.debug("最后请求时间："+DateTypeConverter.toDefaultDateTime(latest.getEndTime()));
        long totalTime=latest.getEndTime().getTime()-first.getStartTime().getTime();
        LOG.debug("系统总时间："+latest.getEndTime().getTime()+"-"+first.getStartTime().getTime()+"="+totalTime);
        AtomicLong processTime=new AtomicLong();
        models.forEach(item -> {
            LOG.debug("      增加请求处理时间："+item.getProcessTime());
            processTime.addAndGet(item.getProcessTime());
        });
        LOG.debug("处理请求时间：" + processTime.get());
        long waitTime=totalTime-processTime.get();
        LOG.debug("系统空闲时间："+waitTime);
        data.put("处理请求时间", processTime.get());
        data.put("系统空闲时间", -waitTime);
        
        return data;
    }
    public static LinkedHashMap<String,Long> getTopData(List<ProcessTime> models, int top){       
        //同一命令只留最耗时的命令
        models=mini(models);
        
        LinkedHashMap<Long,ProcessTime> data=new LinkedHashMap<>();
        //将日志数据转换为统计报表数据
        models.forEach(item -> {
            data.put(item.getProcessTime(), item);
        });
        LinkedHashMap<String,Long> result=new LinkedHashMap<>();
        AtomicInteger i=new AtomicInteger();
        data.keySet().stream().sorted((a,b) -> b.compareTo(a)).forEach(processTime -> {
            if(i.incrementAndGet()>top){
                return;
            }
            String newKey=DateTypeConverter.toDefaultDateTime(data.get(processTime).getStartTime())+", "+data.get(processTime).getResource();
            result.put(newKey, processTime);
        });
        return result;
    }
    public static LinkedHashMap<String,Long> getUserTimeData(List<ProcessTime> models){        
        LinkedHashMap<String,Long> temp=new LinkedHashMap<>();
        //将日志数据转换为统计报表数据
        models.forEach(item -> {
            String username=item.getUsername();
            if(username == null){
                username = "匿名用户";
            }
            
            Long value=temp.get(username);
            if(value==null){
                value=item.getProcessTime();
            }else{
                value+=item.getProcessTime();
            }
            
            temp.put(username,value);
        });
        return temp;
    }
    
    public static LinkedHashMap<String,Long> getSequenceDataSS(List<ProcessTime> models){    
        return getSequenceTimeData(models,"yyyy-MM-dd HH:mm:ss");
    }
    public static LinkedHashMap<String,Long> getSequenceDataMM(List<ProcessTime> models){     
        return getSequenceTimeData(models,"yyyy-MM-dd HH:mm");
    }
    public static LinkedHashMap<String,Long> getSequenceDataHH(List<ProcessTime> models){    
        return getSequenceTimeData(models,"yyyy-MM-dd HH");
    }
    public static LinkedHashMap<String,Long> getSequenceDataDD(List<ProcessTime> models){    
        return getSequenceTimeData(models,"yyyy-MM-dd");
    }
    public static LinkedHashMap<String,Long> getSequenceDataMonth(List<ProcessTime> models){    
        return getSequenceTimeData(models,"yyyy-MM");
    }
    private static LinkedHashMap<String,Long> getSequenceTimeData(List<ProcessTime> models,String format){        
        LinkedHashMap<String,ProcessTime> temp=new LinkedHashMap<>();
        //将日志数据转换为统计报表数据
        models.forEach(item -> {
            String key=new SimpleDateFormat(format).format(item.getStartTime());
            ProcessTime value=temp.get(key);
            if(value==null){
                value=item;
            }else{
                value=value.getProcessTime()>item.getProcessTime()?value:item;
            }
            
            temp.put(key,value);
        });
        LinkedHashMap<String,Long> LinkedHashMap=new LinkedHashMap<>();
        temp.keySet().forEach(key -> {
            LinkedHashMap.put(key+", "+temp.get(key).getResource(), temp.get(key).getProcessTime());
        });
        return LinkedHashMap;
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