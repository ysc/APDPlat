package com.apdplat.platform.util;

import com.apdplat.platform.log.APDPlatLogger;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author ysc
 */
public class SortUtils {
    protected static final APDPlatLogger log = new APDPlatLogger(SortUtils.class);
        
    private SortUtils(){};
    
    public static Map.Entry[] getSortedMapByValue(Map map) {
        Set set = map.entrySet();
        Map.Entry[] entries = (Map.Entry[]) set.toArray(new Map.Entry[set.size()]);
        Arrays.sort(entries, new Comparator() {

            @Override
            public int compare(Object arg0, Object arg1) {
                Integer key1 = Integer.valueOf(((Map.Entry) arg0).getValue().toString());
                Integer key2 = Integer.valueOf(((Map.Entry) arg1).getValue().toString());
                return key2.compareTo(key1);
            }
        });
        return entries;
    }    
}
