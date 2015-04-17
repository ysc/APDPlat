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

package org.apdplat.platform.search;

import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.util.ConvertUtils;
import java.util.Locale;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 索引重建
 * @author 杨尚川
 */
@Service
public class IndexRebuilder {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(IndexRebuilder.class);


    /**
     * 同步重建索引
     * 1、非线程安全
     * 如需要线程安全，请使用IndexManager类的rebuidAll方法
     * 2、同步调用
     * 阻塞用户线程直到索引建立完毕
     * 
     * @return 是否重建成功
     */
    public boolean build(){
        try{
            LOG.info("开始建立索引文件...");
            LOG.info("Start to create index file...", Locale.ENGLISH);
            long beginTime = System.currentTimeMillis();
            float max=(float)Runtime.getRuntime().maxMemory()/1000000;
            float total=(float)Runtime.getRuntime().totalMemory()/1000000;
            float free=(float)Runtime.getRuntime().freeMemory()/1000000;
            String pre="执行之前剩余内存:"+max+"-"+total+"+"+free+"="+(max-total+free);
            String preEn="Remain memory before execution:"+max+"-"+total+"+"+free+"="+(max-total+free);

            //在这里重建索引
            //.........

            long costTime = System.currentTimeMillis() - beginTime;
            max=(float)Runtime.getRuntime().maxMemory()/1000000;
            total=(float)Runtime.getRuntime().totalMemory()/1000000;
            free=(float)Runtime.getRuntime().freeMemory()/1000000;
            String post="执行之后剩余内存:"+max+"-"+total+"+"+free+"="+(max-total+free);
            String postEn="Remain memory after execution:"+max+"-"+total+"+"+free+"="+(max-total+free);
            LOG.info("索引文件建立完毕");
            LOG.info("Finish build index", Locale.ENGLISH);
            LOG.info("耗时:" + ConvertUtils.getTimeDes(costTime));
            LOG.info("Elapsed:" + ConvertUtils.getTimeDes(costTime), Locale.ENGLISH);
            LOG.info(pre);
            LOG.info(preEn, Locale.ENGLISH);
            LOG.info(post);
            LOG.info(postEn, Locale.ENGLISH);
        }catch(Exception e){
            LOG.error("建立索引出错", e);
            LOG.error("Failed in building index", e, Locale.ENGLISH);
            return false;
        }
        return true;
    }
}