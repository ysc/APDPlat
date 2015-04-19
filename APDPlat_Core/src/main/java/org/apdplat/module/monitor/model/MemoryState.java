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

package org.apdplat.module.monitor.model;

import org.apdplat.platform.generator.ActionGenerator;
import org.apdplat.platform.annotation.IgnoreBusinessLog;
import org.apdplat.platform.annotation.IgnoreUser;
import org.apdplat.platform.annotation.ModelAttr;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.apdplat.platform.annotation.Database;
import org.apdplat.platform.model.Model;
import org.apdplat.platform.search.annotations.Searchable;
import org.apdplat.platform.search.annotations.SearchableProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 *不需要保存该模型的增删改日志
 * 不需要自动设置模型的添加用户
 * @author 杨尚川
 */
@Entity
@Scope("prototype")
@Component
@Searchable
@IgnoreBusinessLog
@IgnoreUser
@Database("log")
public class MemoryState extends Model {
    /**
     * 服务器IP地址
     */
    @ModelAttr("服务器IP地址")
    protected String serverIP;

    @ModelAttr("应用系统名称")
    protected String appName;
    
    @Temporal(TemporalType.TIMESTAMP)
    @SearchableProperty(format="yyyy-MM-dd")
    @ModelAttr("记录时间")
    protected Date recordTime;

    @ModelAttr("最大可用内存")
    protected Float maxMemory;

    @ModelAttr("已分配内存")
    protected Float totalMemory;
    
    @ModelAttr("已释放内存")
    protected Float freeMemory;
    
    @ModelAttr("可用内存")
    protected Float usableMemory;
    
    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public Float getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(Float freeMemory) {
        this.freeMemory = freeMemory;
    }

    public Float getMaxMemory() {
        return maxMemory;
    }

    public void setMaxMemory(Float maxMemory) {
        this.maxMemory = maxMemory;
    }

    public Float getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(Float totalMemory) {
        this.totalMemory = totalMemory;
    }

    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }

    public Float getUsableMemory() {
        return usableMemory;
    }

    public void setUsableMemory(Float usableMemory) {
        this.usableMemory = usableMemory;
    }

    @Override
    public String getMetaData() {
        return "内存使用情况日志";
    }
    public static void main(String[] args){
        MemoryState obj=new MemoryState();
        //生成Action
        ActionGenerator.generate(obj.getClass());
    }
}