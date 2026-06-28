package com.example.demo;

import java.time.Instant;
import java.util.UUID;

/**
 * 一个待审查的错误实例。
 */
public class ReviewError {

    public enum Status { PENDING, DISPATCHED, REVIEWED }

    private final String id;
    private final ErrorType type;
    private final String detail;
    private Status status;
    private String assignedReviewerId;
    private Instant createdAt;
    private Instant dispatchedAt;

    public ReviewError(ErrorType type, String detail) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.type = type;
        this.detail = detail;
        this.status = Status.PENDING;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public ErrorType getType() { return type; }
    public String getDetail() { return detail; }
    public Status getStatus() { return status; }
    public String getAssignedReviewerId() { return assignedReviewerId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getDispatchedAt() { return dispatchedAt; }

    public void markDispatched(String reviewerId) {
        this.status = Status.DISPATCHED;
        this.assignedReviewerId = reviewerId;
        this.dispatchedAt = Instant.now();
    }

    public void markReviewed() {
        this.status = Status.REVIEWED;
    }

    @Override
    public String toString() {
        return "ReviewError{id='" + id + "', type=" + type + ", status=" + status
                + ", reviewer=" + assignedReviewerId + "}";
    }
}
