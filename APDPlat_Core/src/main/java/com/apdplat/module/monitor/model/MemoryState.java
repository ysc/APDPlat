package com.apdplat.module.monitor.model;

import com.apdplat.platform.generator.ActionGenerator;
import com.apdplat.platform.annotation.IgnoreBusinessLog;
import com.apdplat.platform.annotation.IgnoreUser;
import com.apdplat.platform.annotation.ModelAttr;
import com.apdplat.platform.model.Model;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableProperty;
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