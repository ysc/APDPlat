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

import java.util.List;
import javax.annotation.Resource;
import org.apdplat.module.system.model.APDPlatConfig;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.apdplat.platform.service.ServiceFacade;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

/**
 * 数据库和配置文件同步服务
 * @author 杨尚川
 */
@Service
public class APDPlatConfigSyncService  implements ApplicationListener{
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(APDPlatConfigSyncService.class);
    
    @Resource
    private ServiceFacade serviceFacade;

    @Override
    public void onApplicationEvent(ApplicationEvent event){
        if(event instanceof ContextRefreshedEvent){
            LOG.info("spring容器初始化完成, 开始检查是否启用数据库配置，如果启用数据库配置，则数据库中的配置信息有最高优先级，会覆盖配置文件的配置信息");
            if(dbEnable()){
                LOG.info("启用数据库配置，同步数据库，将配置文件中独有的配置信息加入数据库");
                if(overrideDB()){
                    LOG.info("删除数据库中的所有配置信息");
                    clearDbConfig();
                }
                syncToDB();
                if(!overrideDB()){
                    syncFromDB();
                }
            }else{
                LOG.info("未启用数据库配置");
            }
        }
    }
    private boolean dbEnable(){
        return PropertyHolder.getBooleanProperty("config.db.enable");
    }
    private boolean overrideDB(){
        return PropertyHolder.getBooleanProperty("config.db.override");
    }
    /**
     * 删除数据库中的所有配置信息
     */
    private void clearDbConfig(){
        List<APDPlatConfig> configs = serviceFacade.query(APDPlatConfig.class).getModels();
        int len = configs.size();
        if(len < 1){
            return;
        }
        Integer[] ids = new Integer[len];
        for(int i=0; i< len; i++){
            ids[i] = configs.get(i).getId();
        }
        serviceFacade.delete(APDPlatConfig.class, ids);
    }
    /**
     * 将配置文件里面的配置信息导入数据库，如果数据库中已经存在相应的配置信息，则忽略导入
     */
    private void syncToDB(){
        PropertyHolder.getProperties().entrySet().forEach(entry -> {
            APDPlatConfig config = new APDPlatConfig();
            config.setConfigKey(entry.getKey());
            config.setConfigValue(entry.getValue());
            try {
                serviceFacade.create(config);
                LOG.info("成功将配置项 " + entry.getKey() + "=" + entry.getValue() + " 加入数据库");
            } catch (Exception e) {
                LOG.info("配置项 " + entry.getKey() + " 已经存在于数据库中，配置文件中的值不会覆盖数据库中的值，如需覆盖，则启用配置config.db.override=true");
            }
        });
    }
    /**
     * 用数据库里面的配置信息覆盖配置文件里面的配置信息
     */
    private void syncFromDB(){
        List<APDPlatConfig> configs = serviceFacade.query(APDPlatConfig.class).getModels();
        LOG.info("从数据库中加载的配置信息数目："+configs.size());
        configs.forEach(config -> {
            LOG.info("旧值（配置文件）："+config.getConfigKey()+"="+PropertyHolder.getProperty(config.getConfigKey()));
            LOG.info("新值（数据库）："+config.getConfigKey()+"="+config.getConfigValue());
            PropertyHolder.setProperty(config.getConfigKey(), config.getConfigValue());
        });
    }
}
