package com.apdplat.platform.util;

import com.apdplat.platform.log.APDPlatLogger;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author ysc
 */
public class XMLUtils {
    protected static final APDPlatLogger log = new APDPlatLogger(XMLUtils.class);

    private XMLUtils() {
    }

    ;
    /**
     *
     * @param xml 类路径资源
     * @return 是否验证通过
     */
    public static boolean validateXML(String xml) {
        if (!xml.startsWith("/")) {
            xml = "/" + xml;
        }
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(true);
            DocumentBuilder builder = dbf.newDocumentBuilder();
            builder.parse(new InputSource(FileUtils.getAbsolutePath("/WEB-INF/classes" + xml)));
            return true;
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            log.error("验证XML失败",ex);
        }
        return false;
    }

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
