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

import org.apache.commons.lang.StringUtils;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 密码长度安全策略
 * 密码长度必须大于等于6
 * @author 杨尚川
 */
@Service
public class PasswordLengthStrategy implements PasswordStrategy{
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(PasswordLengthStrategy.class);

    @Override
    public void check(String password) throws PasswordInvalidException {
        if(StringUtils.isBlank(password) || password.length() < 6){
            String message = "密码长度必须大于等于6";
            LOG.error(message);
            throw new PasswordInvalidException(message);
        }
        LOG.info("密码符合安全策略");
    }
}
