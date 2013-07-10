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

package org.apdplat.module.system.service;

import java.util.ArrayList;
import java.util.List;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.model.Model;
import org.apdplat.platform.service.ServiceFacade;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.apdplat.platform.util.ConvertUtils;
import org.springframework.stereotype.Service;

/**
 *
 * @author 杨尚川
 */
@Service
public class LogQueue {
    private static final APDPlatLogger LOG = new APDPlatLogger(LogQueue.class);
    //使用日志数据库
    @Resource(name = "serviceFacadeForLog")
    private ServiceFacade serviceFacade;
    private static ConcurrentLinkedQueue <Model> logs =  new  ConcurrentLinkedQueue <>();
    private static int logQueueMax=Integer.parseInt(PropertyHolder.getProperty("logQueueMax"));
    public static synchronized void addLog(Model log){
        logs.add(log);
        if(logs.size()>logQueueMax){
            queue.saveLog();
        }
    }
    private static LogQueue queue=null;
    public static LogQueue getLogQueue(){
        return queue;
    }
    @PostConstruct
    public void execute(){
        queue=this;
    }
    public synchronized void saveLog(){
        int len=logs.size();
        if(len==0){
            LOG.info("没有日志需要保存");
            return ;
        }
        LOG.info("保存前队列中的日志数目为(Num. of log before saving in the queue)："+len);
        long start=System.currentTimeMillis();
        List<Model> list=new ArrayList<>();
        for(int i=0;i<len;i++){
            list.add(logs.remove());            
        }
        try{
            serviceFacade.create(list);
            long cost=System.currentTimeMillis()-start;
            LOG.info("成功保存(Success to save) "+len+" 条日志(logs),耗时(cost): "+ConvertUtils.getTimeDes(cost));
            LOG.info("保存后队列中的日志数目为(Num. of log after saving in the queue)："+logs.size());
        }catch(Exception e){
            LOG.error("保存日志失败(Failed to save log)",e);
        }
    }
}