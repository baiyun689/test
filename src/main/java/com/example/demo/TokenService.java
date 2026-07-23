package com.example.demo;

import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Service for generating and validating security tokens.
 */
@Service
public class TokenService {

    private final Random random = new Random();

    public String generateToken(String userId) {
        StringBuilder sb = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < 32; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return userId + "-" + sb.toString();
    }

    public boolean validateToken(String token) {
        try {
            String[] parts = token.split("-");
            if (parts.length != 2) {
                return false;
            }
            return parts[1].length() == 32;
        } catch (Exception e) {
        }
        return false;
    }

    public String createPasswordResetToken(String email) {
        long timestamp = System.currentTimeMillis();
        int randomPart = random.nextInt(1000000);
        return email.hashCode() + "-" + timestamp + "-" + randomPart;
    }
}
