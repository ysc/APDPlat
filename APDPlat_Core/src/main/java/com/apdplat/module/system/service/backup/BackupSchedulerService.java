package com.apdplat.module.system.service.backup;

import com.apdplat.module.system.model.BackupScheduleConfig;
import com.apdplat.platform.log.APDPlatLogger;
import com.apdplat.platform.result.Page;
import com.apdplat.platform.service.ServiceFacade;
import java.text.ParseException;
import javax.annotation.Resource;
import org.quartz.CronExpression;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.quartz.CronTriggerBean;
import org.springframework.stereotype.Service;
/**
 * 在系统启动的时候，检查是否需要执行定时备份数据
 * @author ysc
 */
@Service
public class BackupSchedulerService implements ApplicationListener {
    protected static final APDPlatLogger log = new APDPlatLogger(BackupSchedulerService.class);

    private static SchedulerFactory sf = new StdSchedulerFactory();
    @Resource(name = "serviceFacade")
    protected ServiceFacade serviceFacade;
    @Resource(name = "backupTask")
    private JobDetail backupTask;

    /**
     * 系统启动的时候执行此方法
     * @param event 
     */
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            log.info("spring容器初始化完成, 开始检查是否需要启动定时备份数据调度器");
            BackupScheduleConfig config = getBackupScheduleConfig();
            if (config != null && config.isEnabled()) {
                schedule(config.getScheduleHour(),config.getScheduleMinute());
                log.info("启动定时备份数据调度器");
            }else{
                log.info("没有设置定时备份数据任务");
            }
        }
    }
    /**
     * 获取备份数据库调度配置
     * @return 备份数据库调度配置
     */
    public BackupScheduleConfig getBackupScheduleConfig(){        
        Page<BackupScheduleConfig> page=serviceFacade.query(BackupScheduleConfig.class);
        if(page.getTotalRecords()==1){
            BackupScheduleConfig scheduleConfig=page.getModels().get(0);  
            return scheduleConfig;
        }
        return null;
    }
    /**
     * 取消定时备份任务
     * @return  提示信息
     */
    public String unSchedule(){        
        try {
            BackupScheduleConfig config=getBackupScheduleConfig();
            if(config!=null){
                config.setEnabled(false);
                serviceFacade.update(config);
                log.info("禁用定时重建配置对象");
            }else{
                String tip="还没有设置定时备份数据任务";
                log.info(tip);
                return tip;
            }
            Scheduler sched = sf.getScheduler();
            sched.deleteJob(backupTask.getName(), "DEFAULT");
            String tip="删除定时备份数据任务，任务名为：" + backupTask.getName() + ",全名为: " + backupTask.getFullName();
            log.info(tip);
            return tip;
        } catch (SchedulerException ex) {
            String tip="删除定时备份数据任务失败，原因："+ex.getMessage();
            log.info(tip);
            return tip;
        }
    }
    /**
     * 定时备份数据库（24小时制）
     * @param hour 几点
     * @param minute 几分
     * @return 
     */
    public String schedule(int hour, int minute) {
        BackupScheduleConfig scheduleConfig = getBackupScheduleConfig();
        if (scheduleConfig == null) {
            //新建配置对象
            BackupScheduleConfig config = new BackupScheduleConfig();
            config.setScheduleHour(hour);
            config.setScheduleMinute(minute);
            config.setEnabled(true);
            serviceFacade.create(config);
        } else {
            //修改配置对象
            scheduleConfig.setScheduleHour(hour);
            scheduleConfig.setScheduleMinute(minute);
            scheduleConfig.setEnabled(true);
            serviceFacade.update(scheduleConfig);
        }

        String expression = "0 " + minute + " " + hour + " * * ?";
        try {
            CronExpression cronExpression = new CronExpression(expression);

            CronTrigger trigger = new CronTriggerBean();
            trigger.setCronExpression(cronExpression);
            trigger.setName("定时触发器,时间为：" + hour + ":" + minute);

            Scheduler sched = sf.getScheduler();
            sched.deleteJob(backupTask.getName(), "DEFAULT");
            sched.scheduleJob(backupTask, trigger);
            sched.start();
            String tip = "删除上一次的任务，任务名为：" + backupTask.getName() + ",全名为: " + backupTask.getFullName();
            log.info(tip);
            String taskState = "定时备份数据任务执行频率为每天，时间（24小时制）" + hour + ":" + minute;
            log.info(taskState);
            return taskState;
        } catch (ParseException | SchedulerException e) {
            String tip = "定时备份数据设置失败，原因：" + e.getMessage();
            log.info(tip,e);
            return tip;
        }
    }
}
