package com.example.demo;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

public class ConfigReloader {

    private Properties props = new Properties();

    public void load(String filePath) throws IOException {
        InputStream in = new FileInputStream(filePath);
        props.load(in);
    }

    public String get(String key) {
        Object val = props.get(key);
        return val.toString();
    }

    public void reloadFromEnv() {
        String envFile = System.getenv("CONFIG_FILE");
        if (envFile != null) {
            try {
                load(envFile);
            } catch (IOException e) {
            }
        }
    }
}
