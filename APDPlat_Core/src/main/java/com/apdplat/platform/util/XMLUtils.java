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

import com.apdplat.platform.log.APDPlatLogger;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *验证XML是否合法
 * @author ysc
 */
public class XMLUtils {
    protected static final APDPlatLogger log = new APDPlatLogger(XMLUtils.class);

    private XMLUtils() {
    }

    /**
     *验证类路径资源中的XML是否合法
     * @param xml 类路径资源
     * @return 是否验证通过
     */
    public static boolean validateXML(String xml) {
        if (!xml.startsWith("/")) {
            xml = "/" + xml;
        }
        String xmlPath = FileUtils.getAbsolutePath("/WEB-INF/classes" + xml);
        try {
            InputStream in = new FileInputStream(xmlPath);
            return validateXML(in);
        } catch (FileNotFoundException ex) {
            log.error("构造XML文件失败", ex);
        }
        return false;
    }
    /**
     *验证类路径资源中的XML是否合法
     * @param in XML文件输入流
     * @return 是否验证通过
     */
    public static boolean validateXML(InputStream in) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(true);
            DocumentBuilder builder = dbf.newDocumentBuilder();
            builder.parse(new InputSource(in));
            return true;
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            log.error("验证XML失败",ex);
        }
        return false;
    }
}