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

package org.apdplat.platform.service;

import org.apdplat.platform.log.APDPlatLogger;
import java.io.IOException;
import java.io.StringWriter;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 *
 * @author 杨尚川
 */
public abstract class ChartService {
    protected final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(getClass());

    protected String formatXML(Element rootElement) {
        StringWriter writer = new StringWriter();
        try {
            Document chartDocument = new Document(rootElement);
            XMLOutputter outputter = new XMLOutputter();
            outputter.setFormat(Format.getPrettyFormat());
            outputter.output(chartDocument, writer);
        } catch (IOException e) {
            LOG.error("保生成XML出错",e);
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