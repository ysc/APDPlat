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

package com.apdplat.module.system.service.backup;

import com.apdplat.module.system.service.PropertyHolder;
import com.apdplat.platform.util.SpringContextUtils;
import org.springframework.stereotype.Service;

/**
 *执行备份恢复的服务，自动判断使用的是什么数据库，并找到该数据库备份恢复服务的实现并执行
 * @author ysc
 */
@Service
public class BackupServiceExecuter extends BackupService{  
    private BackupService backupService=null;
    /**
     * 查找并执行正在使用的数据的备份实现实例
     * @return 
     */
    @Override
    public boolean backupImpl() {
        if(backupService==null){
            backupService=SpringContextUtils.getBean(PropertyHolder.getProperty("jpa.database"));
        }
        return backupService.backupImpl();
    }
    /**
     * 查找并执行正在使用的数据的恢复实现实例
     * @param date
     * @return 
     */
    @Override
    public boolean restoreImpl(String date) {
        if(backupService==null){
            backupService=SpringContextUtils.getBean(PropertyHolder.getProperty("jpa.database"));
        }
        return backupService.restoreImpl(date);
    }    
}