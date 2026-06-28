package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 错误分发器 —— 核心调度逻辑。
 *
 * 接收一个 ReviewError,根据其 ErrorType 找到唯一的 Reviewer,
 * 将错误分发给该审查员。整个分发过程只依赖 1:1 映射,不做任何
 * 负载均衡或轮询 —— 因为理论上每种错误就只有一个审查员负责。
 */
@Service
public class ErrorDispatcher {

    private static final Logger log = LoggerFactory.getLogger(ErrorDispatcher.class);

    private final ReviewerAssignmentService assignmentService;

    /** 分发历史记录,供测试验证 */
    private final List<DispatchRecord> history = new CopyOnWriteArrayList<>();

    public ErrorDispatcher(ReviewerAssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    /**
     * 分发一个错误给对应的审查员。
     *
     * @param error 待分发的错误
     * @return 分发记录
     * @throws IllegalStateException 如果该 ErrorType 尚未分配审查员
     */
    public DispatchRecord dispatch(ReviewError error) {
        Reviewer reviewer = assignmentService.getReviewer(error.getType())
            .orElseThrow(() -> new IllegalStateException(
                "错误类型 " + error.getType() + " 尚未分配审查员,无法分发"));

        error.markDispatched(reviewer.getId());

        DispatchRecord record = new DispatchRecord(error, reviewer);
        history.add(record);
        log.info("分发: {} -> {}", error.getId(), reviewer.getName());
        return record;
    }

    /** 批量分发 */
    public List<DispatchRecord> dispatchAll(List<ReviewError> errors) {
        List<DispatchRecord> records = new ArrayList<>();
        for (ReviewError e : errors) {
            records.add(dispatch(e));
        }
        return records;
    }

    /** 查看分发历史 */
    public List<DispatchRecord> getHistory() {
        return List.copyOf(history);
    }

    /** 清空历史 (用于测试复位) */
    public void clearHistory() {
        history.clear();
    }

    // ------------------------------------------------------------------ record

    /** 一次分发操作的记录 */
    public record DispatchRecord(ReviewError error, Reviewer reviewer) {
        public ErrorType errorType() { return error.getType(); }
        public String reviewerName() { return reviewer.getName(); }
        public String reviewerId() { return reviewer.getId(); }
    }
}
