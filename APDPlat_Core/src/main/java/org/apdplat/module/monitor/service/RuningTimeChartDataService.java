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
import org.apdplat.module.monitor.model.RuningTime;
import org.apdplat.platform.action.converter.DateTypeConverter;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.util.ConvertUtils;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import org.apdplat.platform.log.APDPlatLoggerFactory;

/**
 *
 * @author 杨尚川
 */
public class RuningTimeChartDataService {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(RuningTimeChartDataService.class);
    
    public static LinkedHashMap<String,Long> getRuningSequence(List<RuningTime> models){
        models=getValidData(models);
        LinkedHashMap<String,Long> data=new LinkedHashMap<>();
        if(models.size()<1){
            return data;
        }
        Collections.sort(models, new Comparator(){

            @Override
            public int compare(Object o1, Object o2) {
                RuningTime p1=(RuningTime)o1;
                RuningTime p2=(RuningTime)o2;
                return (int) (p1.getStartupTime().getTime()-p2.getStartupTime().getTime());
            }
        
        });
        for(int i=0;i<models.size();){
            RuningTime item = models.get(i);
            String key=DateTypeConverter.toDefaultDateTime(item.getStartupTime())+", 运行"+item.getRuningTimeStr();
            data.put(key, item.getRuningTime());
            i++;
            if(i<models.size()){
                RuningTime item2 = models.get(i);
                long stop=item2.getStartupTime().getTime()-item.getShutdownTime().getTime();
                key=DateTypeConverter.toDefaultDateTime(item.getShutdownTime())+", 停机"+ConvertUtils.getTimeDes(stop);
                data.put(key, -stop);
            }
        }
        
        return data;
    }
    
    public static LinkedHashMap<String,Long> getRuningRateData(List<RuningTime> models){
        models=getValidData(models);
        LinkedHashMap<String,Long> data=new LinkedHashMap<>();
        if(models.size()<1){
            return data;
        }
        Collections.sort(models, new Comparator(){

            @Override
            public int compare(Object o1, Object o2) {
                RuningTime p1=(RuningTime)o1;
                RuningTime p2=(RuningTime)o2;
                return (int) (p1.getStartupTime().getTime()-p2.getStartupTime().getTime());
            }
        
        });
        RuningTime first=models.get(0);
        RuningTime latest=models.get(models.size()-1);
        LOG.debug("系统首次启动时间："+DateTypeConverter.toDefaultDateTime(first.getStartupTime()));
        LOG.debug("系统最后关闭时间："+DateTypeConverter.toDefaultDateTime(latest.getShutdownTime()));
        long totalTime=latest.getShutdownTime().getTime()-first.getStartupTime().getTime();
        LOG.debug("系统总时间："+latest.getShutdownTime().getTime()+"-"+first.getStartupTime().getTime()+"="+totalTime);
        long runingTime=0;
        for(RuningTime item : models){
            LOG.debug("      增加系统运行时间："+item.getRuningTime());
            runingTime+=item.getRuningTime();
        }
        LOG.debug("系统运行时间："+runingTime);
        long stopTime=totalTime-runingTime;
        LOG.debug("系统停机时间："+stopTime);
        data.put("运行时间", runingTime);
        data.put("停机时间", -stopTime);
        
        return data;
    }
    public static List<RuningTime> getValidData(List<RuningTime> runingTimes){
        List<RuningTime> models = new ArrayList<>();
        for(RuningTime runingTime : runingTimes){            
            //如果系统启动时间或是关闭时间有一项为空，则忽略
            if(runingTime.getStartupTime() != null && runingTime.getShutdownTime() != null){
                models.add(runingTime);
            }
        }
        return models;
    }
}