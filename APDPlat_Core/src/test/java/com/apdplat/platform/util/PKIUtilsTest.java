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

package com.apdplat.platform.util;

import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author ysc
 */
public class PKIUtilsTest {

     //private String cert = "/com/apdplat/module/security/pki/apdplat_public.crt";
     private String cert = "/com/apdplat/module/security/pki/apdplat.crt";
     private String store = "/com/apdplat/module/security/pki/apdplat.keystore";
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