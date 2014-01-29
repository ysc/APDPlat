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

import org.apdplat.module.system.service.PropertyHolder;
import org.apdplat.platform.action.converter.DateTypeConverter;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;
import org.apdplat.module.system.service.backup.AbstractBackupService;
import org.springframework.stereotype.Service;

/**
 *MySQL备份恢复实现
 * @author 杨尚川
 */
@Service("MYSQL")
public class MySQLBackupService extends AbstractBackupService{
 
    /**
     * MySQL备份数据库实现
     * @return 
     */
    @Override
    public boolean backup() {
        try {
            String path=getBackupFilePath()+DateTypeConverter.toFileName(new Date())+".bak";
            String command=PropertyHolder.getProperty("db.backup.command");
            command=command.replace("${db.username}", username);
            command=command.replace("${db.password}", password);
            command=command.replace("${module.short.name}", PropertyHolder.getProperty("module.short.name"));

            Runtime runtime = Runtime.getRuntime();
            Process child = runtime.exec(command);
            InputStream in = child.getInputStream();

            try(OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path), "utf8");BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf8"))){
                String line=reader.readLine();
                while (line != null) {
                    writer.write(line+"\n");
                    line=reader.readLine();
                }
                writer.flush();
            }
            LOG.debug("备份到："+path);
            return true;
        } catch (Exception e) {
            LOG.error("备份出错",e);
        }
        return false;
    }

    /**
     * MySQL恢复数据库实现
     * @param date
     * @return 
     */
    @Override
    public boolean restore(String date) {
        try {
            String path=getBackupFilePath()+date+".bak";
            String command=PropertyHolder.getProperty("db.restore.command");
            command=command.replace("${db.username}", username);
            command=command.replace("${db.password}", password);
            command=command.replace("${module.short.name}", PropertyHolder.getProperty("module.short.name"));
            
            Runtime runtime = Runtime.getRuntime();
            Process child = runtime.exec(command);
            try(OutputStreamWriter writer = new OutputStreamWriter(child.getOutputStream(), "utf8");BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "utf8"))){
                String line=reader.readLine();
                while (line != null) {
                    writer.write(line+"\n");
                    line=reader.readLine();
                }
                writer.flush();
            }
            LOG.debug("从 "+path+" 恢复");
            return true;
        } catch (Exception e) {
            LOG.error("恢复出错",e);
        }
        return false;
    }
}