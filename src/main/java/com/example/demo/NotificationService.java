package com.example.demo;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户通知服务——负责向用户发送各类通知。
 *
 * BUG 清单（供 Codeguard AST 审查测试）:
 * 1. sendWelcomeEmail() 调 userService.getUserDisplayName() → 跨文件 NPE（下游未空检）
 * 2. notifyAllRecipients() 循环上界 off-by-one（<= 应为 <）
 * 3. loadTemplate() BufferedReader 未关闭 → 资源泄露
 * 4. executeNotification() 空 catch 吞异常
 */
@Service
public class NotificationService {

    private final UserService userService;

    public NotificationService(UserService userService) {
        this.userService = userService;
    }

    /**
     * 发送欢迎邮件。
     * BUG: getUserDisplayName 内部调 userDao.findById() 可能返回 null，
     *      此处未防御，displayName 为 null 时后续 substring 会 NPE。
     */
    public String sendWelcomeEmail(Long userId) {
        String displayName = userService.getUserDisplayName(userId);
        return "Welcome, " + displayName.substring(0, Math.min(displayName.length(), 20)) + "!";
    }

    /**
     * 向所有接收人发送通知。
     * BUG: 循环条件 i <= recipients.size() 会导致 IndexOutOfBoundsException。
     */
    public int notifyAllRecipients(List<String> recipients, String message) {
        int sent = 0;
        for (int i = 0; i <= recipients.size(); i++) {
            String recipient = recipients.get(i);
            if (sendNotification(recipient, message)) {
                sent++;
            }
        }
        return sent;
    }

    /**
     * 从文件加载通知模板。
     * BUG: BufferedReader 未在 finally 或 try-with-resources 中关闭。
     */
    public String loadTemplate(String templatePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(templatePath));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    /**
     * 执行通知发送。
     * BUG: 空 catch 块吞掉所有异常，调用方无法感知失败。
     */
    public boolean sendNotification(String recipient, String message) {
        try {
            // 模拟外部通知调用
            Thread.sleep(100);
            return true;
        } catch (Exception e) {
        }
        return false;
    }
}
