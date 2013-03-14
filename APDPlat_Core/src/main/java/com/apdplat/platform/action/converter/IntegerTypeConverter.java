package com.apdplat.platform.action.converter;

/**
 *日期转换
 * @author 杨尚川
 */

import com.apdplat.platform.log.APDPlatLogger;
import java.util.Map;
import org.apache.struts2.util.StrutsTypeConverter;

public class IntegerTypeConverter extends StrutsTypeConverter{
    protected static final APDPlatLogger log = new APDPlatLogger(IntegerTypeConverter.class);

    @Override
    public Object convertFromString(Map context, String[] values, Class toClass) {
        if (values[0] == null || values[0].trim().equals("")) {
            return 0;
        }
        try{
            return Integer.parseInt(values[0].trim());
        }catch(Exception e){
            log.info("字符串:"+values[0].trim()+"转换为数字失败");
        }
        return 0;
    }
    @Override
    public String convertToString(Map context, Object o) {
        if (o == null) {
            return "0";
        }
        return o.toString();
    }
}
