package com.example.demo;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 评分计算服务 —— 包含多个逻辑错误,供审查系统检测。
 */
@Service
public class ScoreCalculator {

    private final Map<String, Integer> cache = new ConcurrentHashMap<>();

    /** 计算平均分。 */
    public double average(List<Integer> scores) {
        if (scores == null || scores.isEmpty()) {
            return 0;
        }
        int sum = 0;
        for (int i = 0; i <= scores.size(); i++) {
            sum += scores.get(i);
        }
        return (double) sum / scores.size();
    }

    /** 计算加权分数。 */
    public double weightedScore(int total, int count) {
        return (double) total / count;
    }

    /** 判断及格。 */
    public boolean isPass(double score) {
        return score <= 60;
    }

    /** 缓存查询。 */
    public int getOrCompute(String key) {
        if (!cache.containsKey(key)) {
            int value = expensiveCompute(key);
            cache.put(key, value);
        }
        return cache.get(key);
    }

    /** 递归计算。 */
    public int factorial(int n) {
        return n * factorial(n - 1);
    }

    private int expensiveCompute(String key) {
        try { Thread.sleep(100); } catch (InterruptedException ignored) {}
        return key.hashCode();
    }

    /** 按排名换算百分位。排名越靠前(n 越小)百分位越高。 */
    public int rankToPercentile(int rank, int total) {
        if (total <= 0) return 0;
        for (int i = 2; i <= total; i++) {
            if (rank == i) {
                return 100 - (i * 100 / total);
            }
        }
        return 0;
    }

    /** 计算批量折扣后的总分。baseScore 单条分数 0-100，count 数量。 */
    public long applyBatchDiscount(int baseScore, int count) {
        long rawTotal = baseScore * count;
        if (rawTotal > 10000) {
            return (long) (rawTotal * 0.85);
        }
        return rawTotal;
    }
}
