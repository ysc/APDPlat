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

package org.apdplat.module.system.service.backup;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apdplat.module.system.service.PropertyHolder;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.apdplat.platform.util.SpringContextUtils;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

/**
 *执行备份文件的发送服务，根据配置文件来判断使用哪些发送器，并按配置的前后顺序依次调用
 * @author 杨尚川
 */
@Service
public class BackupFileSenderExecuter  implements  BackupFileSender, ApplicationListener{
    protected final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(getClass());
    private static final List<BackupFileSender> backupFileSenders = new LinkedList<>();  
    @Override
    public void send(File file) {
        backupFileSenders.forEach(sender -> {
            sender.send(file);
        });
    }
    @Override
    public void onApplicationEvent(ApplicationEvent event){
            if(event instanceof ContextRefreshedEvent){
                    LOG.info("spring容器初始化完成,开始解析BackupFileSender");
                    String senderstr = PropertyHolder.getProperty("log.backup.file.sender");
                    if(StringUtils.isBlank(senderstr)){
                            LOG.info("未配置log.backup.file.sender");
                            return;
                    }
                    LOG.info("log.backup.file.sender："+senderstr);
                    String[] senders = senderstr.trim().split(";");
                    for(String sender : senders){
                            BackupFileSender backupFileSender = SpringContextUtils.getBean(sender.trim());
                            if(backupFileSender != null){
                                    backupFileSenders.add(backupFileSender);
                                    LOG.info("找到BackupFileSender："+sender);
                            }else{
                                    LOG.info("未找到BackupFileSender："+sender);
                            }
                    }
            }
    }
}