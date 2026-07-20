package com.example.demo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DataImporter {

    public List<String[]> parseXml(String xmlContent) throws Exception {
        List<String[]> result = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new java.io.ByteArrayInputStream(xmlContent.getBytes()));
        NodeList rows = doc.getElementsByTagName("row");
        for (int i = 0; i < rows.getLength(); i++) {
            Element row = (Element) rows.item(i);
            NodeList cells = row.getElementsByTagName("cell");
            String[] values = new String[cells.getLength()];
            for (int j = 0; j < cells.getLength(); j++) {
                values[j] = cells.item(j).getTextContent();
            }
            result.add(values);
        }
        return result;
    }

    public void extractZip(String zipPath, String destDir) throws IOException {
        File dest = new File(destDir);
        dest.mkdirs();
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipPath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File outFile = new File(destDir, entry.getName());
                if (entry.isDirectory()) {
                    outFile.mkdirs();
                } else {
                    outFile.getParentFile().mkdirs();
                    FileOutputStream fos = new FileOutputStream(outFile);
                    byte[] buf = new byte[4096];
                    int n;
                    while ((n = zis.read(buf)) != -1) {
                        fos.write(buf, 0, n);
                    }
                }
                zis.closeEntry();
            }
        }
    }

    public String importAndPreview(String filePath) throws IOException {
        byte[] data = Files.readAllBytes(Paths.get(filePath));
        return new String(data);
    }
}
