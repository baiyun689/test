package com.example.demo;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.*;

@RestController
public class FileUploadController {

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file,
                         @RequestParam("dir") String dir) throws Exception {
        Path uploadPath = Paths.get("/var/uploads/" + dir);
        Files.createDirectories(uploadPath);
        String filename = file.getOriginalFilename();
        Path target = uploadPath.resolve(filename);
        file.transferTo(target.toFile());
        return "OK";
    }

    @GetMapping("/log")
    public String viewLog(@RequestParam String name) throws Exception {
        String cmd = "cat /var/logs/" + name;
        Process p = Runtime.getRuntime().exec(cmd);
        return new String(p.getInputStream().readAllBytes());
    }
}
