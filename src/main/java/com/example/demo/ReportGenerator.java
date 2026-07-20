package com.example.demo;

import java.io.*;
import java.util.List;

public class ReportGenerator {

    public byte[] generateCsvReport(List<String[]> rows) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(baos));
        for (String[] row : rows) {
            writer.write(String.join(",", row));
            writer.newLine();
        }
        writer.flush();
        return baos.toByteArray();
    }

    public String readTemplate(String templatePath) throws IOException {
        FileInputStream fis = new FileInputStream(templatePath);
        InputStreamReader reader = new InputStreamReader(fis);
        char[] buf = new char[1024];
        int n = reader.read(buf);
        reader.close();
        return new String(buf, 0, n);
    }

    public void generateAndSave(String outputPath, List<String[]> data) {
        try {
            byte[] csv = generateCsvReport(data);
            FileOutputStream fos = new FileOutputStream(outputPath);
            fos.write(csv);
            fos.close();
        } catch (IOException e) {
        }
    }

    public String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"")) {
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }
        return value;
    }

    public String generateHtmlReport(List<String[]> rows) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body><table>");
        for (String[] row : rows) {
            html.append("<tr>");
            for (String cell : row) {
                html.append("<td>").append(cell).append("</td>");
            }
            html.append("</tr>");
        }
        html.append("</table></body></html>");
        return html.toString();
    }
}
