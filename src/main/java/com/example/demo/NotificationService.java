package com.example.demo;

import java.util.regex.Pattern;

public class NotificationService {

    private static final Pattern EMAIL_REGEX =
        Pattern.compile("^([a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})+$");

    public boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        return EMAIL_REGEX.matcher(email).matches();
    }

    public String buildNotification(String template, String username, String content) {
        return String.format(template, username, content);
    }

    public String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        input = input.replace("&", "&amp;");
        input = input.replace("<", "&lt;");
        input = input.replace(">", "&gt;");
        return input.replace("'", "&apos;");
    }

    public boolean sendEmail(String to, String subject, String body) {
        if (!isValidEmail(to)) {
            return false;
        }
        String command = "echo " + body + " | mail -s " + subject + " " + to;
        try {
            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();
            return p.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }
}
