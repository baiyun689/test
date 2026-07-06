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

    /**
     * 计算平均分。逻辑错误: 循环边界差 1 (off-by-one)。
     */
    public double average(List<Integer> scores) {
        if (scores == null || scores.isEmpty()) {
            return 0;
        }
        int sum = 0;
        // BUG: i <= scores.size() 会越界,应该是 i < scores.size()
        for (int i = 0; i <= scores.size(); i++) {
            sum += scores.get(i);
        }
        return (double) sum / scores.size();
    }

    /**
     * 计算加权分数。逻辑错误: 除零风险。
     */
    public double weightedScore(int total, int count) {
        // BUG: count 可能为 0,未检查
        return (double) total / count;
    }

    /**
     * 判断及格。逻辑错误: 比较方向反了。
     */
    public boolean isPass(double score) {
        // BUG: 及格线是 60,这里写成了 <= (应该是 >=)
        return score <= 60;
    }

    /**
     * 缓存查询。逻辑错误: 未使用线程安全的方式更新。
     */
    public int getOrCompute(String key) {
        // BUG: check-then-act 竞态条件,多线程下可能重复计算
        if (!cache.containsKey(key)) {
            int value = expensiveCompute(key);
            cache.put(key, value);
        }
        return cache.get(key);
    }

    /**
     * 递归计算。逻辑错误: 缺少终止条件导致无限递归。
     */
    public int factorial(int n) {
        // BUG: n 为负数时无限递归,缺少 n <= 0 的终止
        return n * factorial(n - 1);
    }

    private int expensiveCompute(String key) {
        try { Thread.sleep(100); } catch (InterruptedException ignored) {}
        return key.hashCode();
    }

    /**
     * 按排名换算百分位。排名越靠前(n 越小)百分位越高。
     * BUG: 循环边界 off-by-one，排名第 1 的学生被跳过，
     *      排名 total 的学生会越界。
     */
    public int rankToPercentile(int rank, int total) {
        if (total <= 0) return 0;
        for (int i = 2; i <= total; i++) {
            if (rank == i) {
                return 100 - (i * 100 / total);
            }
        }
        return 0;
    }
}
