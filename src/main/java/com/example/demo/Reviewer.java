package com.example.demo;

/**
 * 审查员。每个审查员负责审查特定类型的错误。
 * 约束: 一个 ErrorType 只对应一个 Reviewer (1:1)。
 */
public class Reviewer {

    private final String id;
    private final String name;
    private final ErrorType assignedErrorType;

    public Reviewer(String id, String name, ErrorType assignedErrorType) {
        this.id = id;
        this.name = name;
        this.assignedErrorType = assignedErrorType;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public ErrorType getAssignedErrorType() { return assignedErrorType; }

    @Override
    public String toString() {
        return "Reviewer{id='" + id + "', name='" + name + "', type=" + assignedErrorType + "}";
    }
}
