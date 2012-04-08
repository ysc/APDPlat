package com.apdplat.module.monitor.service;

import com.apdplat.module.monitor.model.MemoryState;
import com.apdplat.module.system.service.LogQueue;
import com.apdplat.module.system.service.SystemListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ysc
 */
public class MemoryMonitorThread extends Thread{
    protected static final Logger log = LoggerFactory.getLogger(MemoryMonitorThread.class);
    public boolean running=true;
    private int circle=10;
    public MemoryMonitorThread(int circle){
        this.setDaemon(true);
        this.setName("内存监视线程");
        log.info("内存监视间隔为 "+circle+" 分钟");
        this.circle=circle;
    }
    
    @Override
    public void run(){
        log.info("内存监视线程启动");
        while(running){
            log();
            try {
                Thread.sleep(circle*60*1000);
            } catch (InterruptedException ex) {
                if(!running){
                    log.info("内存监视线程退出");
                }else{
                    ex.printStackTrace();
                }
            }
        }
    }
    private void log(){        
        float max=(float)Runtime.getRuntime().maxMemory()/1000000;
        float total=(float)Runtime.getRuntime().totalMemory()/1000000;
        float free=(float)Runtime.getRuntime().freeMemory()/1000000;
        
        MemoryState log=new MemoryState();
        try {
            log.setServerIP(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        log.setAppName(SystemListener.getContextPath());
        log.setRecordTime(new Date());
        log.setMaxMemory(max);
        log.setTotalMemory(total);
        log.setFreeMemory(free);
        log.setUsableMemory(log.getMaxMemory()-log.getTotalMemory()+log.getFreeMemory());
        LogQueue.addLog(log);
    }
}
