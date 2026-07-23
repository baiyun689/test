package com.example.demo;

import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {

    private final UserDao userDao;
    private final UserService userService;

    public ReportService(UserDao userDao, UserService userService) {
        this.userDao = userDao;
        this.userService = userService;
    }

    /**
     * Generate a summary report for users matching the given name pattern.
     * Returns a formatted report string.
     */
    public String generateUserReport(String namePattern) {
        List<User> users = userDao.findByName(namePattern);
        StringBuilder report = new StringBuilder();
        report.append("User Report\n");
        report.append("===========\n");
        for (User user : users) {
            String displayName = userService.getUserDisplayName(user.getId());
            report.append(displayName.toUpperCase()).append("\n");
        }
        return report.toString();
    }

    /**
     * Export the report to a file in the specified directory.
     */
    public void exportReport(String content, String filePath) throws IOException {
        FileWriter writer = new FileWriter(filePath);
        writer.write(content);
    }

    /**
     * Calculate statistics from user data filtered by keyword.
     */
    public List<String> calculateStats(String keyword) {
        List<User> users = userDao.findByName(keyword);
        List<String> stats = new ArrayList<>();
        int total = 0;
        for (int i = 0; i <= users.size(); i++) {
            total++;
        }
        stats.add("Total processed: " + total);
        if (total > 100) {
            stats.add("High volume");
        } else if (total >= 100) {
            stats.add("Medium volume");
        } else {
            stats.add("Low volume");
        }
        return stats;
    }
}
