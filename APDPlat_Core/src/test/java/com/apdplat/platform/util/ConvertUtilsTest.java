package com.apdplat.platform.util;

import static junit.framework.Assert.*;
import org.junit.Test;

/**
 *
 * @author ysc
 */
public class ConvertUtilsTest {
    @Test
    public void testConvert(){
        String str="apdplat应用级开发平台（杨尚川）";
        String hexStr=ConvertUtils.byte2HexString(str.getBytes());
        byte[] bytStr=ConvertUtils.hexString2ByteArray(hexStr);
        String newStr=new String(bytStr);
        assertEquals(str,newStr);
    }
}
