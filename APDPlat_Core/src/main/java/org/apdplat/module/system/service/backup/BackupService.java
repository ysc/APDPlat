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
import java.util.List;

/**
 * 备份恢复数据库接口
 * @author 杨尚川
 */
public interface BackupService {
    /**
     * 备份数据库
     * @return 是否备份成功
     */
    public boolean backup();
    /**
     * 恢复数据库
     * @param date
     * @return 是否恢复成功
     */
    public boolean restore(String date);
    /**
     * 获取已经存在的备份文件名称列表
     * @return  备份文件名称列表
     */
    public List<String> getExistBackupFileNames();    
    /**
     * 获取备份文件存放的本地文件系统路径
     * @return 备份文件存放路径
     */
    public String getBackupFilePath();
    /**
     * 获取最新的备份文件
     * @return 最新的备份文件
     */
    public File getNewestBackupFile();
}
