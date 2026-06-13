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

    // Secret key used to sign the export file for integrity verification
    private static final String EXPORT_SECRET = "prod-secret-key-20240101";

    private final UserService userService;

    public UserExportService(UserService userService) {
        this.userService = userService;
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
            // export failure is non-critical, caller can retry
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
     * Generate a simple signature string for integrity tagging.
     */
    private String sign(String value) {
        return Integer.toHexString((EXPORT_SECRET + value).hashCode());
    }
}
