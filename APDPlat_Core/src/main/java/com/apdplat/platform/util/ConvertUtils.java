package com.apdplat.platform.util;

import com.apdplat.platform.log.APDPlatLogger;

/**
 *
 * @author ysc
 */
public class ConvertUtils {
    protected static final APDPlatLogger log = new APDPlatLogger(ConvertUtils.class);

    private ConvertUtils() {
    }
    
    public static String getTimeDes(long ms) {
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = ms / dd;
        long hour = (ms - day * dd) / hh;
        long minute = (ms - day * dd - hour * hh) / mi;
        long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;
        
        StringBuilder str=new StringBuilder();
        if(day>0){
            str.append(day).append("天,");
        }
        if(hour>0){
            str.append(hour).append("小时,");
        }
        if(minute>0){
            str.append(minute).append("分钟,");
        }
        if(second>0){
            str.append(second).append("秒,");
        }
        if(milliSecond>0){
            str.append(milliSecond).append("毫秒,");
        }
        if(str.length()>0){
            str=str.deleteCharAt(str.length()-1);
        }

        return str.toString();
    }

    public static String byte2HexString(byte[] b) {
        char[] hex = {'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] newChar = new char[b.length * 2];
        for (int i = 0; i < b.length; i++) {
            newChar[2 * i] = hex[(b[i] & 0xf0) >> 4];
            newChar[2 * i + 1] = hex[b[i] & 0xf];
        }
        return new String(newChar);
    }

    public static byte[] hexString2ByteArray(String hexString) {
        char[] chars = hexString.toCharArray();
        byte[] b = new byte[chars.length / 2];
        for (int i = 0; i < b.length; i++) {
            int high = Character.digit(chars[2 * i], 16) << 4;
            int low = Character.digit(chars[2 * i + 1], 16);
            b[i] = (byte) (high | low);
        }
        return b;
    }
}
