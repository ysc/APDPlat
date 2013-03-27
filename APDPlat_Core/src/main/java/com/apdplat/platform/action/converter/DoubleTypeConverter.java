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

package com.apdplat.platform.action.converter;

/**
 *日期转换
 * @author 杨尚川
 */

import com.apdplat.platform.log.APDPlatLogger;
import java.util.Map;
import org.apache.struts2.util.StrutsTypeConverter;

public class DoubleTypeConverter extends StrutsTypeConverter{
    protected static final APDPlatLogger log = new APDPlatLogger(DoubleTypeConverter.class);


    @Override
    public Object convertFromString(Map context, String[] values, Class toClass) {
        if (values[0] == null || values[0].trim().equals("")) {
            return 0;
        }
        try{
            return Double.parseDouble(values[0].trim());
        }catch(Exception e){
            log.info("字符串:"+values[0].trim()+"转换为数字失败");
        }
        return 0;
    }
    @Override
    public String convertToString(Map context, Object o) {
        if (o == null)
            return "0";
        return o.toString();
    }
}