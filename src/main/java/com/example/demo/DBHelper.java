package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBHelper {
    public static Connection getConnection(String password) {
        try {
            return DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
