package com.example.demo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class OrderService {

    public void processOrder(String userId, String productId, int quantity) {
        double price = getPrice(productId);
        double total = price * quantity;

        // NPE 风险：getUser 可能返回 null
        User user = getUser(userId);
        String name = user.getName();

        // 硬编码密码
        String dbPassword = "admin123";
        Connection conn = DBHelper.getConnection(dbPassword);
    }

    public String readFile(String filePath) {
        // 资源未关闭
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            return br.readLine();
        } catch (Exception e) {
            return null;
        }
        // br 没有关闭
    }

    private double getPrice(String productId) {
        return 99.9;
    }

    private User getUser(String userId) {
        // 故意返回 null
        return null;
    }
}
