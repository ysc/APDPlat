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

package org.apdplat.module.system.action;

import org.apdplat.module.system.model.BackupScheduleConfig;
import org.apdplat.module.system.service.backup.BackupSchedulerService;
import org.apdplat.platform.action.ActionSupport;
import org.apdplat.platform.util.FileUtils;
import org.apdplat.platform.util.ZipUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.apdplat.module.system.service.backup.BackupService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Scope("prototype")
@Controller
@RequestMapping("/system/backup/")
public class BackupAction extends ActionSupport {
    @Resource
    private BackupService backupServiceExecuter;    
    @Resource
    private BackupSchedulerService backupSchedulerService;

    @ResponseBody
    @RequestMapping("query.action")
    public String query(){
        Map map=new HashMap();
        BackupScheduleConfig config=null;
        try{
                config=backupSchedulerService.getBackupScheduleConfig();
        }catch(Exception e){
                LOG.warn("未获取到备份配置对象",e);
        }
        
        if(config!=null && config.isEnabled()){
            map.put("state", "定时备份数据任务执行频率为每天，时间（24小时制）"+config.getScheduleHour()+":"+config.getScheduleMinute());
            map.put("hour",config.getScheduleHour());
            map.put("minute", config.getScheduleMinute());

        }else{
            map.put("state", "无定时调度任务");
        }
        return toJson(map);
    }
    @ResponseBody
    @RequestMapping("clearTask.action")
    public String clearTask(){
        String result=backupSchedulerService.unSchedule();
        return result;
    }
    @ResponseBody
    @RequestMapping("setTask.action")
    public String setTask(@RequestParam int hour,
                          @RequestParam int minute){     
        if(-1<hour && hour<24 && -1<minute && minute<60){
           String result=backupSchedulerService.schedule(hour, minute);
            return result;
        }
        return "调度时间不正确";
    }
    @ResponseBody
    @RequestMapping("store.action")
    public String store(){
        List<String> existBackup=backupServiceExecuter.getExistBackupFileNames();
        List<Map<String,String>> data=new ArrayList<>();
        existBackup.forEach(item -> {
            Map<String, String> map = new HashMap<>();
            map.put("value", item);
            map.put("text", item);
            data.add(map);
        });
        return toJson(data);
    }
    @ResponseBody
    @RequestMapping("backup.action")
    public String backup(){
        if(backupServiceExecuter.backup()){
            return "true";
        }else{
            return "false";
        }
    }
    @ResponseBody
    @RequestMapping("download.action")
    public String download(@RequestParam String date){        
        if(date==null || "".equals(date.trim())){
            LOG.info("请指定下载备份数据库的时间点");
            return null;
        }
        date= date.replace(" ", "-").replace(":", "-");
        
        //生成一个临时目录
        String destPath = "/platform/temp/backup/" + System.currentTimeMillis();
        File outputFile = new File( FileUtils.getAbsolutePath(destPath));
        outputFile.mkdirs();
        
        outputFile=new File(outputFile, date+".zip");
        //获取备份文件
        String backupFile=backupServiceExecuter.getBackupFilePath()+date+".bak";
        //生成一个临时压缩文件
        ZipUtils.createZip(backupFile, outputFile.getAbsolutePath());

        return destPath+"/"+date+".zip";
    }
    @ResponseBody
    @RequestMapping("restore.action")
    public String restore(@RequestParam String date){
        if(date==null || "".equals(date.trim())){
            LOG.info("请指定恢复数据库到哪一个时间点");
            return null;
        }
        date= date.replace(" ", "-").replace(":", "-");
        if(backupServiceExecuter.restore(date)){
            return "true";
        }
        return "false";
    }
}