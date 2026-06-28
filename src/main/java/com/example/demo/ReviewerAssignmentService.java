package com.example.demo;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 维护 ErrorType -> Reviewer 的 1:1 映射。
 *
 * 核心约束: 每种 ErrorType 最多分配给一个 Reviewer。
 * 尝试重复分配同一 ErrorType 直接抛异常 —— 这就是你要测试的"理论上只应分配给一个审查员"。
 */
@Service
public class ReviewerAssignmentService {

    @PostConstruct
    void init() {
        initDefaults();
    }

    /** ErrorType -> Reviewer 的 1:1 映射 */
    private final Map<ErrorType, Reviewer> assignments = new ConcurrentHashMap<>();

    /** Reviewer ID -> Reviewer 的索引 */
    private final Map<String, Reviewer> reviewerIndex = new ConcurrentHashMap<>();

    /**
     * 为一个 ErrorType 分配审查员。
     *
     * @throws IllegalStateException 如果该 ErrorType 已被分配给另一个审查员
     */
    public Reviewer assign(ErrorType type, Reviewer reviewer) {
        Reviewer existing = assignments.putIfAbsent(type, reviewer);
        if (existing != null && !existing.getId().equals(reviewer.getId())) {
            throw new IllegalStateException(
                String.format("错误类型 %s 已分配给审查员 %s, 不能重复分配给 %s。"
                    + "每种错误类型只允许一个审查员。",
                    type, existing.getName(), reviewer.getName()));
        }
        reviewerIndex.put(reviewer.getId(), reviewer);
        return assignments.get(type);
    }

    /** 查询某 ErrorType 对应的审查员 (可能为 null,表示尚未分配) */
    public Optional<Reviewer> getReviewer(ErrorType type) {
        return Optional.ofNullable(assignments.get(type));
    }

    /** 获取当前所有分配 */
    public Map<ErrorType, Reviewer> getAllAssignments() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(assignments));
    }

    /** 预置: 为所有逻辑错误类型分配审查员,每个类型一个人 */
    public void initDefaults() {
        assign(ErrorType.NULL_POINTER,       new Reviewer("R001", "张三", ErrorType.NULL_POINTER));
        assign(ErrorType.OFF_BY_ONE,         new Reviewer("R002", "李四", ErrorType.OFF_BY_ONE));
        assign(ErrorType.RACE_CONDITION,     new Reviewer("R003", "王五", ErrorType.RACE_CONDITION));
        assign(ErrorType.DEAD_LOCK,          new Reviewer("R004", "赵六", ErrorType.DEAD_LOCK));
        assign(ErrorType.INFINITE_RECURSION, new Reviewer("R005", "孙七", ErrorType.INFINITE_RECURSION));
        assign(ErrorType.WRONG_COMPARATOR,   new Reviewer("R006", "周八", ErrorType.WRONG_COMPARATOR));
    }
}
