package com.example.demo;

/**
 * 代码审查中的错误类型 —— 目前仅收录逻辑错误。
 *
 * 每种类型理论上只应分配给一个审查员,保证审查责任清晰不重叠。
 * 非逻辑错误(如 SQL 注入、路径遍历等安全类)不在本枚举中 ——
 * 提交这类错误时,调度员应拒绝分发(无审查员负责)。
 */
public enum ErrorType {

    NULL_POINTER("空指针风险", "未判空直接调用"),
    OFF_BY_ONE("边界偏移错误", "循环/数组索引差1"),
    RACE_CONDITION("竞态条件", "多线程无同步访问共享状态"),
    DEAD_LOCK("死锁", "两个线程互相等待对方释放锁"),
    INFINITE_RECURSION("无限递归", "缺少递归终止条件"),
    WRONG_COMPARATOR("比较逻辑错误", "排序比较器违反传递性");

    private final String description;
    private final String detail;

    ErrorType(String description, String detail) {
        this.description = description;
        this.detail = detail;
    }

    public String getDescription() { return description; }
    public String getDetail() { return detail; }
}
