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

package org.apdplat.module.index.service;

import org.apdplat.module.index.model.IndexScheduleConfig;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.result.Page;
import org.apdplat.platform.service.ServiceFacade;
import java.text.ParseException;
import javax.annotation.Resource;
import org.apdplat.platform.log.APDPlatLoggerFactory;
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
 * 定时重建索引调度器服务
 * @author 杨尚川
 */
@Service
public class IndexSchedulerService implements ApplicationListener {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(IndexSchedulerService.class);

    private static SchedulerFactory sf = new StdSchedulerFactory();
    @Resource(name = "serviceFacade")
    private ServiceFacade serviceFacade;
    @Resource(name = "indexTask")
    private JobDetail indexTask;

    /**
     * 系统启动的时候获取配置文件,并判断是否需要执行定时重建索引服务
     * @param event 
     */
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            LOG.info("spring容器初始化完成, 开始检查是否需要启动定时索引调度器");
            IndexScheduleConfig config = getIndexScheduleConfig();
            if (config != null && config.isEnabled()) {
                schedule(config.getScheduleHour(),config.getScheduleMinute());
                LOG.info("启动定时重建索引调度器");
            }else{
                LOG.info("没有设置定时重建索引任务");
            }
        }
    }
    /**
     * 获取索引调度配置文件
     * @return 
     */
    public IndexScheduleConfig getIndexScheduleConfig(){        
        Page<IndexScheduleConfig> page=serviceFacade.query(IndexScheduleConfig.class);
        if(page.getTotalRecords()==1){
            IndexScheduleConfig scheduleConfig=page.getModels().get(0);  
            return scheduleConfig;
        }
        return null;
    }
    /**
     * 取消定时重建索引服务
     * @return 
     */
    public String unSchedule(){        
        try {
            IndexScheduleConfig config=getIndexScheduleConfig();
            if(config!=null){
                config.setEnabled(false);
                serviceFacade.update(config);
                LOG.info("禁用定时重建配置对象");
            }else{
                String tip="还没有设置定时重建索引任务";
                LOG.info(tip);
                return tip;
            }
            Scheduler sched = sf.getScheduler();
            sched.deleteJob(indexTask.getName(), "DEFAULT");
            String tip="删除定时重建索引任务，任务名为：" + indexTask.getName() + ",全名为: " + indexTask.getFullName();
            LOG.info(tip);
            return tip;
        } catch (SchedulerException ex) {
            String tip="删除定时重建索引任务失败，原因："+ex.getMessage();
            LOG.info(tip);
            return tip;
        }
    }
    /**
     * 执行定时重建索引服务
     * @param hour 小时（24小时制）
     * @param minute 分钟
     * @return 提示信息
     */
    public String schedule(int hour, int minute) {
        IndexScheduleConfig scheduleConfig = getIndexScheduleConfig();
        if (scheduleConfig == null) {
            //新建配置对象
            IndexScheduleConfig config = new IndexScheduleConfig();
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
            sched.deleteJob(indexTask.getName(), "DEFAULT");
            sched.scheduleJob(indexTask, trigger);
            sched.start();
            String tip = "删除上一次的任务，任务名为：" + indexTask.getName() + ",全名为: " + indexTask.getFullName();
            LOG.info(tip);
            String taskState = "定时重建索引任务执行频率为每天，时间（24小时制）" + hour + ":" + minute;
            LOG.info(taskState);
            return taskState;
        } catch (ParseException | SchedulerException ex) {
            String tip = "定时重建索引设置失败，原因：" + ex.getMessage();
            LOG.info(tip,ex);
            return tip;
        }
    }
}