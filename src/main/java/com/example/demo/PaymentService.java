package com.example.demo;

import java.sql.*;
import java.io.*;

public class PaymentService {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/pay";
    private static final String DB_USER = "admin";
    private static final String DB_PASS = "Pay@dmin#2024!";

    public void processPayment(String userId, String amount, String cardNo) throws Exception {
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        Statement stmt = conn.createStatement();
        String sql = "INSERT INTO payments (user_id, amount, card_no) VALUES ('"
            + userId + "', " + amount + ", '" + cardNo + "')";
        stmt.execute(sql);
    }

    public void exportPayments(String filePath) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }

    public String getCardPrefix(String cardNo) {
        return cardNo.substring(0, 6);
    }
}
