package com.example.demo;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ReportService {

    public String generateReport(String userId, String format) {
        // SQL 注入：拼接用户输入
        String sql = "SELECT * FROM reports WHERE user_id = '" + userId + "'";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBHelper.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getString("content");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 资源未关闭
        return "no data";
    }

    public File exportFile(String path) {
        // 路径遍历
        return new File("/data/exports/" + path);
    }
}
