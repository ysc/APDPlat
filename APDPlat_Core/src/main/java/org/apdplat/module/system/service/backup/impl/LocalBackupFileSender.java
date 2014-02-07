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

package org.apdplat.module.system.service.backup.impl;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apdplat.module.system.service.PropertyHolder;
import org.apdplat.module.system.service.backup.BackupFileSender;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 将备份文件从本地一个目录复制到另一个目录
 * @author 杨尚川
 */
@Service
public class LocalBackupFileSender implements BackupFileSender{
    protected final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(getClass());

    @Override
    public void send(File file) {
        try {
            String dist = PropertyHolder.getProperty("log.backup.file.local.dir");
            LOG.info("备份文件："+file.getAbsolutePath());
            LOG.info("目标目录："+dist);
            FileUtils.copyFile(file, new File(dist,file.getName()));
        } catch (IOException ex) {
            LOG.info("LocalBackupFileSender失败", ex);
        }
    }
}
