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

import org.apdplat.module.monitor.model.MemoryState;
import org.apdplat.module.system.service.SystemListener;
import org.apdplat.platform.log.APDPlatLogger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Locale;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.apdplat.platform.log.BufferLogCollector;

/**
 *
 * @author 杨尚川
 */
public class MemoryMonitorThread extends Thread{
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(MemoryMonitorThread.class);
    public volatile boolean running=true;
    private int circle=10;
    public MemoryMonitorThread(int circle){
        this.setDaemon(true);
        this.setName("内存监视线程(Memory monitor thread)");
        LOG.info("内存监视间隔为 "+circle+" 分钟");
        LOG.info("Memory monitor interval "+circle+" mins", Locale.ENGLISH);
        this.circle=circle;
    }
    
    @Override
    public void run(){
        LOG.info("内存监视线程启动");
        LOG.info("Launch memory monitor thread", Locale.ENGLISH);
        while(running){
            log();
            try {
                Thread.sleep(circle*60*1000);
            } catch (InterruptedException ex) {
                if(!running){
                    LOG.info("内存监视线程退出");
                    LOG.info("Memory monitor thread abort", Locale.ENGLISH);
                }else{
                    LOG.error("内存监视线程出错", ex);
                    LOG.error("Error happens in memory monitor thread", ex, Locale.ENGLISH);
                }
            }
        }
    }
    private void log(){        
        float max=(float)Runtime.getRuntime().maxMemory()/1000000;
        float total=(float)Runtime.getRuntime().totalMemory()/1000000;
        float free=(float)Runtime.getRuntime().freeMemory()/1000000;
        
        MemoryState logger=new MemoryState();
        try {
            logger.setServerIP(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException ex) {
            LOG.error("获取服务器地址出错",ex);
            LOG.error("Can't get server's internet address", ex, Locale.ENGLISH);
        }
        logger.setAppName(SystemListener.getContextPath());
        logger.setRecordTime(new Date());
        logger.setMaxMemory(max);
        logger.setTotalMemory(total);
        logger.setFreeMemory(free);
        logger.setUsableMemory(logger.getMaxMemory()-logger.getTotalMemory()+logger.getFreeMemory());
        BufferLogCollector.collect(logger);
    }
}