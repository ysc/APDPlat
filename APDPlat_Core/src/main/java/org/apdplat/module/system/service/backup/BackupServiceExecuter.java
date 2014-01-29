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

import org.apdplat.module.system.service.PropertyHolder;
import org.apdplat.platform.util.SpringContextUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 *执行备份恢复的服务，自动判断使用的是什么数据库，并找到该数据库备份恢复服务的实现并执行
 * @author 杨尚川
 */
@Service
public class BackupServiceExecuter extends AbstractBackupService{  
    private BackupService backupService=null;
    
    @Resource(name="backupFileSenderExecuter")
    private BackupFileSenderExecuter backupFileSenderExecuter;
    /**
     * 查找并执行正在使用的数据的备份实现实例
     * @return 
     */
    @Override
    public boolean backup() {
        if(backupService==null){
            backupService=SpringContextUtils.getBean(PropertyHolder.getProperty("jpa.database"));
        }
        boolean result = backupService.backup();
        //如果备份成功，则将备份文件发往他处
        if(result){
            backupFileSenderExecuter.send(getNewestBackupFile());
        }
        return result;
    }
    /**
     * 查找并执行正在使用的数据的恢复实现实例
     * @param date
     * @return 
     */
    @Override
    public boolean restore(String date) {
        if(backupService==null){
            backupService=SpringContextUtils.getBean(PropertyHolder.getProperty("jpa.database"));
        }
        return backupService.restore(date);
    }    
}