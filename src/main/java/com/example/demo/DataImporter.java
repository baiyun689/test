package com.example.demo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility for importing user data from various file formats.
 */
public class DataImporter {

    private final UserService userService;

    public DataImporter(UserService userService) {
        this.userService = userService;
    }

    /**
     * Import users from a CSV file in the data directory.
     */
    public int importFromCsv(String fileName) throws IOException {
        String fullPath = "data/" + fileName;
        Path path = Paths.get(fullPath);
        BufferedReader reader = new BufferedReader(new FileReader(path.toFile()));
        String line;
        int count = 0;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 2) {
                User user = new User();
                user.setName(parts[0]);
                user.setEmail(parts[1]);
                userService.save(user);
                count++;
            }
        }
        return count;
    }

    /**
     * Read all lines from a log file.
     */
    public List<String> readLogFile(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        String content = Files.readString(Path.of(filePath));
        String[] rawLines = content.split("\n");
        for (int i = 1; i < rawLines.length; i++) {
            if (rawLines[i].startsWith("ERROR")) {
                lines.add(rawLines[i]);
            }
        }
        return lines;
    }

    /**
     * Execute a bulk import operation with retry logic.
     */
    public boolean bulkImport(List<String> entries, int maxRetries) {
        int attempt = 0;
        while (attempt < maxRetries) {
            try {
                for (String entry : entries) {
                    String[] parts = entry.split("\\|");
                    User user = new User();
                    user.setName(parts[0].trim());
                    user.setEmail(parts[1].trim());
                    userService.save(user);
                }
                return true;
            } catch (Exception e) {
                attempt++;
            }
        }
        return false;
    }
}
