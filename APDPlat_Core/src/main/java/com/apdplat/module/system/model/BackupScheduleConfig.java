package com.apdplat.module.system.model;

import com.apdplat.platform.annotation.IgnoreBusinessLog;
import com.apdplat.platform.model.Model;
import javax.persistence.Entity;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Entity
@Scope("prototype")
@Component
@IgnoreBusinessLog
public class BackupScheduleConfig extends Model{
    protected int scheduleHour=2;
    protected int scheduleMinute=2;
    protected boolean enabled=true;

    public int getScheduleHour() {
        return scheduleHour;
    }

    public void setScheduleHour(int scheduleHour) {
        this.scheduleHour = scheduleHour;
    }

    public int getScheduleMinute() {
        return scheduleMinute;
    }

    public void setScheduleMinute(int scheduleMinute) {
        this.scheduleMinute = scheduleMinute;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String getMetaData() {
        return "定时备份数据配置";
    }
}
