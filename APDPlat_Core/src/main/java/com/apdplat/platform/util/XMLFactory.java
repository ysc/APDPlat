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
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
/**
*在XML和对象之间进行转换
* @author 杨尚川
*/
public class XMLFactory {
    protected static final APDPlatLogger log = new APDPlatLogger(XMLFactory.class);
       
    private XMLFactory(){};

    private Marshaller marshaller;
    private Unmarshaller unmarshaller;

    /**
     * 参数types为所有需要序列化的Root对象的类型.
     */
    public XMLFactory(Class<?>... types) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(types);
            marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
            unmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Java->Xml
     */
    public String marshal(Object root) {
        try {
            StringWriter writer = new StringWriter();
            marshaller.marshal(root, writer);
            return writer.toString();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Xml->Java
     */
    @SuppressWarnings("unchecked")
    public <T> T unmarshal(String xml) {
        try {
            StringReader reader = new StringReader(xml);
            return (T) unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Xml->Java
     */
    @SuppressWarnings("unchecked")
    public <T> T unmarshal(InputStream in) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(in, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        try {
            return (T) unmarshaller.unmarshal(br);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}