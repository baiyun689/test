package com.example.demo;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigLoader {

    public Map<String, Object> loadFromFile(String filePath) {
        Map<String, Object> config = new HashMap<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                config = (Map<String, Object>) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("load config failed: " + e.getMessage());
        }
        return config;
    }

    public Properties loadProperties(String path) {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(path)) {
            props.load(fis);
        } catch (IOException e) {
            System.err.println("load properties failed: " + e.getMessage());
        }
        return props;
    }

    public Map<String, String> parseXmlConfig(String xmlPath) throws Exception {
        Map<String, String> result = new HashMap<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(xmlPath));
        NodeList entries = doc.getElementsByTagName("entry");
        for (int i = 0; i < entries.getLength(); i++) {
            String key = entries.item(i).getAttributes().getNamedItem("key").getTextContent();
            String value = entries.item(i).getTextContent();
            result.put(key, value);
        }
        return result;
    }

    public String readSecretConfig(String path) {
        try {
            byte[] bytes = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(path));
            return new String(bytes);
        } catch (IOException e) {
            return "";
        }
    }
}
