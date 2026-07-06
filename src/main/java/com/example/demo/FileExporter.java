package com.example.demo;

import java.io.*;

public class FileExporter {
    public static void export(String path, String data) throws IOException {
        FileWriter fw = new FileWriter("reports/" + path);
        fw.write(data);
    }
}
