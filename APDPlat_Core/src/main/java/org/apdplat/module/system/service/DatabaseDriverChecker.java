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

package org.apdplat.module.system.service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.apdplat.platform.util.FileUtils;

/**
 * 因为默认提供了10种数据库的JDBC驱动
 * 所以在系统启动的时候调用该类的check
 * 方法把不用的JDBC驱动移动到其他目录
 * @author 杨尚川
 */
public class DatabaseDriverChecker {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(DatabaseDriverChecker.class);
    
    private DatabaseDriverChecker(){}
    
    public static void check(){
        Map<String, String> drivers = new HashMap<>();
        String jdbcDrivers = PropertyHolder.getProperty("db.jdbc.drivers");
        LOG.info("db.jdbc.drivers:"+jdbcDrivers);
        String[] jars = jdbcDrivers.split("[,|，]");
        int i = 1;
        for(String jar : jars){
            LOG.info((i++)+"、"+jar);
            String[] attr = jar.split("[:|：]");
            if(attr == null || attr.length != 2){
                LOG.error("配置错误："+jar);
                continue;
            }
            drivers.put(attr[0], attr[1]);
        }
        String database = PropertyHolder.getProperty("jpa.database");
        LOG.info("当前使用的业务数据库为："+database);
        String forlogDatabase = PropertyHolder.getProperty("jpa.forlog.database");
        LOG.info("当前使用的日志数据库为："+forlogDatabase);
        drivers.remove(database);        
        drivers.remove(forlogDatabase);
        i = 1;
        for(String key : drivers.keySet()){
            String driver = drivers.get(key);
            LOG.info((i++)+"、"+"将不用的数据库驱动移动到备份目录："+key+":"+driver);
            File source = new File(FileUtils.getAbsolutePath("/WEB-INF/lib/"+driver));
            if(!source.exists()){
                LOG.info("文件不存在，忽略移动："+source.getAbsolutePath());
                continue;
            }
            File distPath = new File(FileUtils.getAbsolutePath("/WEB-INF/lib-jdbc-driver-not-in-use"));
            if(!distPath.exists()){
                distPath.mkdir();
                LOG.info("备份目录不存在，创建");
            }
            File dist = new File(distPath, driver);
            FileUtils.copyFile(source, dist);
            if(source.delete()){
                LOG.info("移动成功");
            }else{
                source.deleteOnExit();
                LOG.error("移动成功，但源文件删除未成功，重启JVM使其生效");
            }
        }
        //加速垃圾回收
        drivers.clear();
    }
}
