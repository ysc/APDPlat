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

package org.apdplat.platform.util;

import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author 杨尚川
 */
public class PKIUtilsTest {

     //private String cert = "/org/apdplat/module/security/pki/apdplat_public.crt";
     private String cert = "/org/apdplat/module/security/pki/apdplat.crt";
     private String store = "/org/apdplat/module/security/pki/apdplat.keystore";
     private String plainText = "apdplat应用级开发平台（杨尚川）";
    @Test
    public void testEncryptAndDecrypt1() {
        //公钥加密
        byte[] result = PKIUtils.encryptWithPublicKey(PKIUtilsTest.class.getResourceAsStream(cert), plainText.getBytes());        
        
        //私钥解密
        result = PKIUtils.decryptWithPrivateKey(PKIUtilsTest.class.getResourceAsStream(store), "apdplat_core_module", "apdplat_core_module", "apdplat", result);
        
        Assert.assertEquals(plainText, new String(result));
    }
    @Test
    public void testEncryptAndDecrypt2() {
        //私钥加密
        byte[] result = PKIUtils.encryptWithPrivateKey(PKIUtilsTest.class.getResourceAsStream(store), "apdplat_core_module", "apdplat_core_module", "apdplat", plainText.getBytes());        
        
        //公钥解密
        result = PKIUtils.decryptWithPublicKey(PKIUtilsTest.class.getResourceAsStream(cert), result);
        
        Assert.assertEquals(plainText, new String(result));
    }
    @Test
    public void testSignatureAndVerifySignature() {
        //私钥签名
        byte[] signature = PKIUtils.signature(PKIUtilsTest.class.getResourceAsStream(store), "apdplat_core_module", "apdplat_core_module", "apdplat", plainText.getBytes());

        //公钥验证签名
        boolean correct=PKIUtils.verifySignature(PKIUtilsTest.class.getResourceAsStream(cert),plainText.getBytes(),signature);
        
        Assert.assertTrue(correct);
    }
}