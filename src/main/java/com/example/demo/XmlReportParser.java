package com.example.demo;

import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.*;
import java.io.*;
import java.util.*;

/**
 * Service for parsing XML-based report files.
 */
@Service
public class XmlReportParser {

    public List<Map<String, String>> parseReport(String filePath) throws Exception {
        List<Map<String, String>> records = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new FileReader(filePath)));
        NodeList items = doc.getElementsByTagName("item");
        for (int i = 0; i < items.getLength(); i++) {
            Element item = (Element) items.item(i);
            Map<String, String> record = new HashMap<>();
            NodeList children = item.getChildNodes();
            for (int j = 0; j < children.getLength(); j++) {
                Node child = children.item(j);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    record.put(child.getNodeName(), child.getTextContent());
                }
            }
            records.add(record);
        }
        return records;
    }

    public String readFirstSection(String filePath) throws Exception {
        FileInputStream fis = new FileInputStream(filePath);
        byte[] buffer = new byte[1024];
        int bytesRead = fis.read(buffer);
        if (bytesRead <= 0) {
            return "";
        }
        return new String(buffer, 0, bytesRead);
    }

    public List<String> extractFields(String filePath, String fieldName) throws Exception {
        List<String> values = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(filePath);
        NodeList nodes = doc.getElementsByTagName(fieldName);
        for (int i = 0; i < nodes.getLength(); i++) {
            values.add(nodes.item(i).getTextContent());
        }
        return values;
    }
}
