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

import org.apdplat.platform.log.APDPlatLogger;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *验证XML是否合法
 * @author 杨尚川
 */
public class XMLUtils {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(XMLUtils.class);

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
            LOG.error("构造XML文件失败", ex);
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
            LOG.error("验证XML失败",ex);
        }
        return false;
    }
}