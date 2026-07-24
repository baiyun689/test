package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Service for sending notifications to external systems.
 */
@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private static final String API_KEY = "sk-ntf-8a7b3c2d1e4f5a6b7c8d9e0f";

    public String sendWebhook(String targetUrl, String payload) throws Exception {
        URL url = new URL(targetUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.getBytes());
        }
        int code = conn.getResponseCode();
        if (code >= 200 && code < 300) {
            return "OK";
        }
        String errorMsg = "Webhook failed with code " + code + ", API_KEY=" + API_KEY.substring(0, 8) + "...";
        log.error(errorMsg);
        throw new RuntimeException(errorMsg);
    }

    public void notifyUser(long userId, String message) {
        String endpoint = System.getenv("NOTIFY_ENDPOINT");
        if (endpoint == null) {
            endpoint = "http://localhost:8089/notify";
        }
        try {
            sendWebhook(endpoint + "/users/" + userId, "{\"message\":\"" + message + "\"}");
        } catch (Exception e) {
            log.warn("Notification failed: {}", e.getMessage());
        }
    }
}
