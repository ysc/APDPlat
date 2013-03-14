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
