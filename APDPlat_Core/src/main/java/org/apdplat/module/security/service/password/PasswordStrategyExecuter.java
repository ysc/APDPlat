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

package org.apdplat.module.security.service.password;

import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apdplat.module.system.service.PropertyHolder;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.apdplat.platform.util.SpringContextUtils;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

/**
 * 密码安全策略执行者
 * 根据配置项user.password.strategy
 * 指定的spring bean name
 * 分别执行指定的策略
 * @author 杨尚川
 */
@Service
public class PasswordStrategyExecuter implements PasswordStrategy, ApplicationListener {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(ApplicationListener.class);
    private final List<PasswordStrategy> passwordStrategys = new LinkedList<>();

    @Override
    public void check(String password) throws PasswordInvalidException {
        for(PasswordStrategy passwordStrategy : passwordStrategys){
            passwordStrategy.check(password);
        }
    }
    
    @Override
    public void onApplicationEvent(ApplicationEvent event){
        if(event instanceof ContextRefreshedEvent){
            LOG.info("spring容器初始化完成,开始解析PasswordStrategy");
            String strategy = PropertyHolder.getProperty("user.password.strategy");
            if(StringUtils.isBlank(strategy)){
                LOG.info("未配置user.password.strategy");
                return;
            }
            LOG.info("user.password.strategy："+strategy);
            String[] strategys = strategy.trim().split(";");
            for(String item : strategys){
                PasswordStrategy passwordStrategy = SpringContextUtils.getBean(item.trim());
                if(passwordStrategy != null){
                    passwordStrategys.add(passwordStrategy);
                    LOG.info("找到PasswordStrategy："+passwordStrategy);
                }else{
                    LOG.info("未找到PasswordStrategy："+passwordStrategy);
                }
            }
        }
    }
}
