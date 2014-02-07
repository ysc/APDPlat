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

package org.apdplat.platform.log.handler;

import java.util.List;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.apdplat.platform.model.Model;
import org.springframework.stereotype.Service;

/**
 *
 * @author 杨尚川
 */
@Service
public class ActiveMQLogHandler implements LogHandler{
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(ActiveMQLogHandler.class);

    @Override
    public <T extends Model> void handle(List<T> list) {
        LOG.info("还未实现！");
    }

}
