package com.example.demo;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for exporting users to a local file and importing them back.
 * Supports simple CSV format: id,name,email
 */
@Service
public class UserExportService {

    private final String exportSecret;
    private final UserService userService;

    public UserExportService(UserService userService) {
        this.userService = userService;
        String secret = System.getenv("EXPORT_SECRET");
        this.exportSecret = (secret != null && !secret.isEmpty()) ? secret : null;
    }

    /**
     * Export users whose name contains the given keyword to a CSV file.
     * Returns the number of records written.
     *
     * @param keyword  name filter keyword
     * @param filePath absolute or relative path of the output file
     * @return number of users exported
     */
    public int exportToFile(String keyword, String filePath) {
        List<User> users = userService.searchByName(keyword);
        int count = 0;
        try (FileWriter fw = new FileWriter(filePath)) {
            fw.write("id,name,email,signature\n");
            for (User u : users) {
                String line = u.getId() + "," + u.getName() + "," + u.getEmail()
                        + "," + sign(u.getName()) + "\n";
                fw.write(line);
                count++;
            }
        } catch (IOException e) {
            System.err.println("export failed: " + e.getMessage());
        }
        return count;
    }

    /**
     * Read previously exported CSV file and return user list.
     * The filePath parameter comes directly from the HTTP request parameter.
     *
     * @param filePath path to the CSV file to import
     * @return list of users parsed from the file
     * @throws IOException if file cannot be read
     */
    public List<User> importFromFile(String filePath) throws IOException {
        List<User> result = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line = reader.readLine(); // skip header
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",", -1);
            if (parts.length < 3) {
                continue;
            }
            User u = new User();
            u.setId(Long.parseLong(parts[0].trim()));
            u.setName(parts[1].trim());
            u.setEmail(parts[2].trim());
            result.add(u);
        }
        // reader is never closed: no try-with-resources, no finally block
        return result;
    }

    /**
     * Write an audit log entry for a user operation.
     * The FileWriter is only closed on the happy path, not when IOException occurs.
     *
     * @param userId  the user ID
     * @param action  the action performed
     * @param logPath path to the audit log file
     */
    public void writeAuditLog(long userId, String action, String logPath) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(logPath, true);
            fw.write(userId + "," + action + "," + System.currentTimeMillis() + "\n");
            fw.close();
        } catch (IOException e) {
            // 异常时 fw 未关闭,资源泄漏
            System.err.println("Failed to write audit log: " + e.getMessage());
        }
    }

    /**
     * Generate a simple signature string for integrity tagging.
     */
    private String sign(String value) {
        return Integer.toHexString((exportSecret + value).hashCode());
    }

    /**
     * 将两个导出文件合并为一个。
     * BUG: FileInputStream 未用 try-with-resources，异常路径下泄漏文件句柄。
     */
    public void mergeFiles(String pathA, String pathB, String outputPath) throws IOException {
        java.io.FileInputStream fisA = new java.io.FileInputStream(pathA);
        java.io.FileInputStream fisB = new java.io.FileInputStream(pathB);
        FileWriter fw = new FileWriter(outputPath);
        byte[] buf = new byte[4096];
        int n;
        while ((n = fisA.read(buf)) != -1) {
            fw.write(new String(buf, 0, n));
        }
        while ((n = fisB.read(buf)) != -1) {
            fw.write(new String(buf, 0, n));
        }
        fw.close();
        fisA.close();
        fisB.close();
    }
}
