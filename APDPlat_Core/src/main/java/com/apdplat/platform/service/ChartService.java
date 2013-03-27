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

package com.apdplat.platform.service;

import com.apdplat.platform.log.APDPlatLogger;
import java.io.IOException;
import java.io.StringWriter;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 *
 * @author ysc
 */
public abstract class ChartService {
    protected final APDPlatLogger log = new APDPlatLogger(getClass());

    protected String formatXML(Element rootElement) {
        StringWriter writer = new StringWriter();
        try {
            Document chartDocument = new Document(rootElement);
            XMLOutputter outputter = new XMLOutputter();
            outputter.setFormat(Format.getPrettyFormat());
            outputter.output(chartDocument, writer);
        } catch (IOException e) {
            log.error("保生成XML出错",e);
        }
        return writer.toString();
    }

    protected Element createRootElement(String caption, String subCaption) {
        Element rootElement = new Element("chart");
        rootElement.setAttribute(new Attribute("caption", caption));
        rootElement.setAttribute(new Attribute("subCaption", subCaption));
        return rootElement;
    }
}