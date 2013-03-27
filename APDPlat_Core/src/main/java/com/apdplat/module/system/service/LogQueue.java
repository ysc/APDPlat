/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.apdplat.module.system.service;

import com.apdplat.platform.log.APDPlatLogger;
import com.apdplat.platform.model.Model;
import com.apdplat.platform.service.ServiceFacade;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 *
 * @author ysc
 */
@Service
public class LogQueue {
    protected static final APDPlatLogger log = new APDPlatLogger(LogQueue.class);
    
    @Resource(name = "serviceFacade")
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
        int success=0;
        log.info("保存前队列中的日志数目为(Num. of log before saving in the queue)："+len);
        try{
            for(int i=0;i<len;i++){
                Model model = logs.remove();
                try{
                    serviceFacade.create(model);
                    success++;
                }catch(Exception e){
                    log.error("保存日志失败(Failed to save log):"+model.getMetaData(),e);
                }
            }
        } catch (Exception e) {
            log.error("保存日志抛出异常(Saving log exception)",e);
        }
        log.info("成功保存(Success to save) "+success+" 条日志(log)");
        log.info("保存后队列中的日志数目为(Num. of log after saving in the queue)："+logs.size());
    }
}