package com.apdplat.platform.util;

import java.util.Collection;
import static junit.framework.Assert.*;
import org.junit.Test;

/**
 *
 * @author ysc
 */
public class FileUtilsTest {

    @Test
    public void testFile() {
        String str="apdplat应用级开发平台（杨尚川）";
        String file="target/test.txt";
        
        FileUtils.createAndWriteFile(file, str);
        Collection<String> result=FileUtils.getTextFileContent(file);
        assertEquals(1, result.size());
        assertTrue(result.contains(str));        
    }
}
