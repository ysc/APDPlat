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

package org.apdplat.module.dictionary.service;

import org.apdplat.module.dictionary.model.DicItem;
import org.apdplat.platform.criteria.Operator;
import org.apdplat.platform.criteria.PropertyCriteria;
import org.apdplat.platform.criteria.PropertyEditor;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.apdplat.platform.service.ServiceFacade;
import java.util.List;
import javax.annotation.Resource;
import org.apdplat.platform.criteria.Criteria;
import org.springframework.stereotype.Service;

/**
 *  数据字典项服务
 * @author 杨尚川
 */
@Service
public class DicItemService {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(DicItemService.class);
    @Resource(name="serviceFacade")
    private ServiceFacade serviceFacade;

    /**
     * 根据 数据字典英文名称 以及 数据字典编码 查找 数据字典项
     * 此方法对于遗留数据迁移非常有用
     * @param dicEnglish 数据字典英文名称
     * @param code 数据字典编码
     * @return 数据字典项
     */
    public DicItem getDicItemByCode(String dicEnglish,String code){
        LOG.debug("根据 数据字典英文名称 ["+dicEnglish+"] 以及 数据字典编码 ["+code+"] 查找 数据字典项");
        PropertyCriteria propertyCriteria=new PropertyCriteria(Criteria.and);
        propertyCriteria.addPropertyEditor(new PropertyEditor("dic.english",Operator.eq,dicEnglish));
        propertyCriteria.addPropertyEditor(new PropertyEditor("code",Operator.eq,"String",code));

        List<DicItem> page=serviceFacade.query(DicItem.class, null, propertyCriteria).getModels();
        if(page.size() != 1){
            return null;
        }
        return page.get(0);
    }    
}