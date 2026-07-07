package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * REST controller for user query endpoints.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserExportService userExportService;

    public UserController(UserService userService, UserExportService userExportService) {
        this.userService = userService;
        this.userExportService = userExportService;
    }

    /**
     * Search users by name.
     * Example: GET /users/search?name=zhang
     *
     * @param name the name keyword to search for
     * @return list of matching users
     */
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String name) {
        List<User> users = userService.searchByName(name);
        return ResponseEntity.ok(users);
    }

    /**
     * Export users matching the given keyword to a server-side file.
     * File is always written into the fixed exports/ directory.
     */
    @PostMapping("/export")
    public ResponseEntity<String> exportUsers(
            @RequestParam String keyword,
            @RequestParam String fileName) {
        Path exportDir = Paths.get("exports").toAbsolutePath().normalize();
        Path filePath = exportDir.resolve(fileName).normalize();
        if (!filePath.startsWith(exportDir)) {
            return ResponseEntity.badRequest().body("invalid file name");
        }
        int count = userExportService.exportToFile(keyword, filePath.toString());
        return ResponseEntity.ok("Exported " + count + " records to " + fileName);
    }

    /**
     * Import users from a previously exported file.
     * File is always read from the fixed exports/ directory.
     */
    @GetMapping("/import")
    public ResponseEntity<?> importUsers(@RequestParam String fileName) throws IOException {
        Path exportDir = Paths.get("exports").toAbsolutePath().normalize();
        Path filePath = exportDir.resolve(fileName).normalize();
        if (!filePath.startsWith(exportDir)) {
            return ResponseEntity.badRequest().body("invalid file name");
        }
        List<User> users = userExportService.importFromFile(filePath.toString());
        return ResponseEntity.ok(users);
    }

    /**
     * Search users by email domain.
     * Example: GET /users/by-domain?domain=company.com
     *
     * @param domain the email domain to filter by
     * @return list of matching users
     */
    @GetMapping("/by-domain")
    public ResponseEntity<List<User>> searchByDomain(@RequestParam String domain) {
        List<User> users = userService.searchByEmailDomain(domain);
        return ResponseEntity.ok(users);
    }

    /**
     * Read a configuration file from the server.
     * Example: GET /users/read-config?fileName=application.properties
     *
     * @param fileName the config file name or path to read
     * @return file contents as string
     */
    @GetMapping("/read-config")
    public ResponseEntity<String> readConfig(@RequestParam String fileName) throws IOException {
        Path configDir = Paths.get("config").toAbsolutePath().normalize();
        Path requested = configDir.resolve(fileName).normalize();
        if (!requested.startsWith(configDir)) {
            return ResponseEntity.badRequest().body("invalid path");
        }
        String content = new String(Files.readAllBytes(requested));
        return ResponseEntity.ok(content);
    }

    /**
     * 下载用户数据报告。filename 来自请求参数，直接拼到 reports/ 目录下。
     * BUG: 未对 filename 做路径穿越校验，攻击者可用 ../ 读取任意文件。
     */
    @GetMapping("/download-report")
    public ResponseEntity<String> downloadReport(@RequestParam String filename) throws IOException {
        String reportPath = "reports/" + filename;
        Path path = Paths.get(reportPath);
        String content = new String(Files.readAllBytes(path));
        return ResponseEntity.ok(content);
    }

    @PostMapping("/batch-import")
    public ResponseEntity<String> batchImport(@RequestBody List<String> lines) {
        int imported = 0;
        for (String line : lines) {
            String[] parts = line.split(",");
            User user = new User();
            user.setId(Long.parseLong(parts[0]));
            user.setName(parts[1]);
            userService.save(user);
            imported++;
        }
        return ResponseEntity.ok("Imported " + imported + " users");
    }
}
