package com.example.demo;

import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 用户数据分析服务——负责用户行为统计与报表生成。
 *
 * BUG 清单（供 Codeguard AST 审查测试）:
 * 1. getActiveUsers() 调 userDao.findByName() → 跨文件 SQL 注入（下游拼接 SQL）
 * 2. calculateEngagement() 条件判断赋值 (=) 而非比较 (==)
 * 3. exportReport() FileOutputStream 未关闭 → 资源泄露
 * 4. calculatePercentile() 除零风险（totalUsers 可能为 0）
 */
@Service
public class AnalyticsService {

    private final UserDao userDao;

    public AnalyticsService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * 按名字模糊查询活跃用户。
     * BUG: userDao.findByName() 内部直接拼接 SQL("SELECT ... WHERE name = '" + name + "'"),
     *      此处将外部输入原样传入，构成二阶 SQL 注入漏洞。
     */
    public List<User> getActiveUsers(String namePattern) {
        List<User> users = userDao.findByName(namePattern);
        // 过滤活跃用户
        return users.stream()
                .filter(u -> u.getEmail() != null)
                .toList();
    }

    /**
     * 计算用户参与度分数。
     * BUG: 条件用 > 50 而非 < 50，导致只计算高阈值用户，与业务意图"过滤低质量用户"相反。
     * 正确逻辑应该是 threshold < 50 时跳过计算。
     */
    public double calculateEngagement(long userId, int threshold) {
        double score = 0.0;
        if (threshold > 50) {
            score = 50.0;
            for (int i = 0; i < 10; i++) {
                score += Math.random() * 10;
            }
            if (score > 100) {
                score = 100.0;
            }
        }
        return score;
    }

    /**
     * 导出用户分析报告到文件。
     * BUG: FileOutputStream 未在 finally 或 try-with-resources 中关闭。
     */
    public void exportReport(String filePath, String content) throws IOException {
        FileOutputStream fos = new FileOutputStream(filePath);
        fos.write(content.getBytes());
    }

    /**
     * 计算百分位排名。
     * BUG: totalUsers 可能为 0，除零导致 ArithmeticException。
     */
    public double calculatePercentile(long userId, int totalUsers) {
        int rank = getRank(userId);
        return (double) rank / totalUsers * 100;
    }

    private int getRank(long userId) {
        // 模拟排名查询
        return (int) (userId % 50) + 1;
    }
}
