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
import org.apdplat.module.system.service.backup.AbstractBackupService;
import org.apdplat.platform.action.DefaultAction;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.util.FileUtils;
import org.apdplat.platform.util.Struts2Utils;
import org.apdplat.platform.util.ZipUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.struts2.convention.annotation.Namespace;
import org.apdplat.module.system.service.backup.BackupService;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Scope("prototype")
@Controller
@Namespace("/system")
public class BackupAction extends DefaultAction {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(BackupAction.class);
    
    private String date;
    @Resource(name="backupServiceExecuter")
    private BackupService backupService;    
    @Resource(name="backupSchedulerService")
    private BackupSchedulerService backupSchedulerService;
    private int hour;
    private int minute;
    
    
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
        
        Struts2Utils.renderJson(map);
        return null;
    }
    public String clearTask(){
        String result=backupSchedulerService.unSchedule();
        Struts2Utils.renderText(result);
        return null;
    }
    public String setTask(){     
        if(-1<hour && hour<24 && -1<minute && minute<60){
           String result=backupSchedulerService.schedule(hour, minute);
           Struts2Utils.renderText(result);
        } else{
            Struts2Utils.renderText("调度时间不正确");
        } 
        return null;
    }
    public String store(){
        List<String> existBackup=backupService.getExistBackupFileNames();
        List<Map<String,String>> data=new ArrayList<>();
        for(String item : existBackup){
            Map<String,String> map=new HashMap<>();
            map.put("value", item);
            map.put("text", item);
            data.add(map);
        }
        Struts2Utils.renderJson(data);
        return null;
    }
    public String backup(){
        if(backupService.backup()){
            Struts2Utils.renderText("true");
        }else{
            Struts2Utils.renderText("false");
        }
        return null;
    }
    public String download(){        
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
        String backupFile=backupService.getBackupFilePath()+date+".bak";
        //生成一个临时压缩文件
        ZipUtils.createZip(backupFile, outputFile.getAbsolutePath());
            
        Struts2Utils.renderText(destPath+"/"+date+".zip");
        return null;
    }
    public String restore(){
        if(date==null || "".equals(date.trim())){
            LOG.info("请指定恢复数据库到哪一个时间点");
            return null;
        }
        date= date.replace(" ", "-").replace(":", "-");
        if(backupService.restore(date)){
            Struts2Utils.renderText("true");
        }else{
            Struts2Utils.renderText("false");
        }
        return null;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}