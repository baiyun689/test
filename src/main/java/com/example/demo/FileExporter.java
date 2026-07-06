package com.example.demo;

import java.io.*;

/** BUG: 路径穿越 + 资源未关闭 */
public class FileExporter {
    public void export(String userPath, String data) throws IOException {
        FileWriter fw = new FileWriter("/tmp/" + userPath);
        fw.write(data);
    }
}
