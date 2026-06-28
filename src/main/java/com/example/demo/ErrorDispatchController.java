package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 错误分发 REST 接口 —— 供你手动测试调度逻辑。
 */
@RestController
@RequestMapping("/errors")
public class ErrorDispatchController {

    private final ReviewerAssignmentService assignmentService;
    private final ErrorDispatcher dispatcher;
    private final ErrorScheduler scheduler;

    public ErrorDispatchController(ReviewerAssignmentService assignmentService,
                                   ErrorDispatcher dispatcher,
                                   ErrorScheduler scheduler) {
        this.assignmentService = assignmentService;
        this.dispatcher = dispatcher;
        this.scheduler = scheduler;
    }

    /** GET /errors/types — 查看所有错误类型及其分配的审查员 */
    @GetMapping("/types")
    public ResponseEntity<Map<String, Object>> listTypes() {
        Map<ErrorType, Reviewer> assignments = assignmentService.getAllAssignments();
        Map<String, Object> result = new LinkedHashMap<>();
        for (var entry : assignments.entrySet()) {
            Map<String, String> info = new LinkedHashMap<>();
            info.put("description", entry.getKey().getDescription());
            info.put("reviewerId", entry.getValue().getId());
            info.put("reviewerName", entry.getValue().getName());
            result.put(entry.getKey().name(), info);
        }
        return ResponseEntity.ok(result);
    }

    /** POST /errors/submit?type=NULL_POINTER&detail=... — 提交一个错误 */
    @PostMapping("/submit")
    public ResponseEntity<Map<String, String>> submit(
            @RequestParam String type,
            @RequestParam(defaultValue = "") String detail) {
        ErrorType errorType;
        try {
            errorType = ErrorType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "REJECTED",
                "reason", "未知错误类型: " + type + " (仅接受: " + errorTypeNames() + ")",
                "note", "该类错误未分配给任何审查员,调度员拒绝处理"
            ));
        }
        ReviewError error = new ReviewError(errorType,
                detail.isEmpty() ? errorType.getDescription() : detail);
        scheduler.submit(error);
        return ResponseEntity.ok(Map.of(
            "errorId", error.getId(),
            "type", errorType.name(),
            "status", "SUBMITTED",
            "note", "已提交到调度队列,等待定时分发"
        ));
    }

    private String errorTypeNames() {
        StringBuilder sb = new StringBuilder();
        for (ErrorType t : ErrorType.values()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(t.name());
        }
        return sb.toString();
    }

    /** POST /errors/dispatch-now — 立即分发(不等定时器) */
    @PostMapping("/dispatch-now")
    public ResponseEntity<List<Map<String, String>>> dispatchNow() {
        // 从队列中取出所有 pending 错误
        scheduler.scanAndDispatch();
        List<ErrorDispatcher.DispatchRecord> history = dispatcher.getHistory();
        List<Map<String, String>> result = history.stream()
            .map(r -> Map.of(
                "errorId", r.error().getId(),
                "errorType", r.errorType().name(),
                "reviewerId", r.reviewerId(),
                "reviewerName", r.reviewerName()
            ))
            .toList();
        return ResponseEntity.ok(result);
    }

    /** GET /errors/history — 查看分发历史 */
    @GetMapping("/history")
    public ResponseEntity<List<Map<String, String>>> history() {
        List<ErrorDispatcher.DispatchRecord> records = dispatcher.getHistory();
        List<Map<String, String>> result = records.stream()
            .map(r -> Map.of(
                "errorId", r.error().getId(),
                "errorType", r.errorType().name(),
                "reviewerId", r.reviewerId(),
                "reviewerName", r.reviewerName()
            ))
            .toList();
        return ResponseEntity.ok(result);
    }

    /** POST /errors/reset — 清空队列和历史,回到初始状态 */
    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> reset() {
        scheduler.clearQueue();
        dispatcher.clearHistory();
        return ResponseEntity.ok(Map.of("status", "RESET", "note", "队列和历史已清空"));
    }

    /** GET /errors/pending-count — 查看待分发队列长度 */
    @GetMapping("/pending-count")
    public ResponseEntity<Map<String, Integer>> pendingCount() {
        return ResponseEntity.ok(Map.of("pendingCount", scheduler.pendingCount()));
    }
}
