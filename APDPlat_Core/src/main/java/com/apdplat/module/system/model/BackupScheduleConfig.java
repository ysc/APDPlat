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