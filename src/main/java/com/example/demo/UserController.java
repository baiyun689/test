package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
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
     * Example: POST /users/export?keyword=zhang&filePath=/tmp/users.csv
     *
     * @param keyword  name filter keyword
     * @param filePath server-side file path to write
     * @return number of records exported
     */
    @PostMapping("/export")
    public ResponseEntity<String> exportUsers(
            @RequestParam String keyword,
            @RequestParam String filePath) {
        int count = userExportService.exportToFile(keyword, filePath);
        return ResponseEntity.ok("Exported " + count + " records to " + filePath);
    }

    /**
     * Import users from a previously exported file on the server.
     * Example: GET /users/import?filePath=/tmp/users.csv
     *
     * @param filePath path to the CSV file on the server
     * @return list of imported users
     */
    @GetMapping("/import")
    public ResponseEntity<List<User>> importUsers(@RequestParam String filePath) throws IOException {
        List<User> users = userExportService.importFromFile(filePath);
        return ResponseEntity.ok(users);
    }
}
