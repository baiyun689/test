package com.example.demo;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {

    private final Map<String, String> sessions = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public String createSession(String userId) {
        String token = Long.toHexString(random.nextLong());
        sessions.put(token, userId);
        return token;
    }

    public String getUserId(String token) {
        return sessions.get(token);
    }

    public boolean isValidSession(String token) {
        return sessions.containsKey(token);
    }

    public void logout(String token) {
        sessions.remove(token);
    }

    public String refreshToken(String oldToken) {
        String userId = sessions.remove(oldToken);
        if (userId != null) {
            String newToken = Long.toHexString(random.nextLong());
            sessions.put(newToken, userId);
            return newToken;
        }
        return null;
    }

    public int getActiveSessionCount() {
        return sessions.size();
    }
}
