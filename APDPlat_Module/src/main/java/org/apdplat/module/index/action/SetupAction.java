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

package org.apdplat.module.index.action;

import net.sf.json.JSONArray;
import org.apdplat.module.index.model.IndexScheduleConfig;
import org.apdplat.module.index.service.IndexSchedulerService;
import org.apdplat.platform.action.ExtJSActionSupport;
import org.apdplat.platform.search.IndexManager;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

@Scope("prototype")
@Controller
@RequestMapping("/index")
public class SetupAction extends ExtJSActionSupport {
    @Resource(name="indexManager")
    private IndexManager indexManager;
    @Resource(name="indexSchedulerService")
    private IndexSchedulerService indexSchedulerService;
    
    private int hour;
    private int minute;

    @ResponseBody
    public String query(){
        Map map=new HashMap();
        try{
            IndexScheduleConfig config=indexSchedulerService.getIndexScheduleConfig();

            if(config!=null && config.isEnabled()){
                map.put("state", "定时重建索引任务执行频率为每天，时间（24小时制）"+config.getScheduleHour()+":"+config.getScheduleMinute());
                map.put("hour",config.getScheduleHour());
                map.put("minute", config.getScheduleMinute());

            }else{
                map.put("state", "无定时调度任务");
            }
        }catch(Exception e){
            LOG.error("无定时调度任务", e);
            map.put("state", "无定时调度任务");
        }

        String json = JSONArray.fromObject(map).toString();
        return json;
    }
    @ResponseBody
    public String rebuidAll() {        
        indexManager.rebuidAll();
        return "已将重建索引任务提交给后台";
    }
    @ResponseBody
    public String clearTask(){
        String result=indexSchedulerService.unSchedule();
        return result;
    }
    @ResponseBody
    public String setTask(){     
        if(-1<hour && hour<24 && -1<minute && minute<60){
           String result=indexSchedulerService.schedule(hour, minute);
           return result;
        } else{
           return "调度时间不正确";
        }
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}