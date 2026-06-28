package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 错误调度器 —— 定时扫描待分发错误队列,调用分发器逐个处理。
 *
 * 每 10 秒扫描一次 pending 队列,将新错误分发给对应审查员。
 * 你可以观察控制台日志确认每个错误只被分发给了唯一的审查员。
 */
@Component
public class ErrorScheduler {

    private static final Logger log = LoggerFactory.getLogger(ErrorScheduler.class);

    private final ErrorDispatcher dispatcher;
    private final Queue<ReviewError> pendingQueue = new ConcurrentLinkedQueue<>();

    public ErrorScheduler(ErrorDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    /** 提交一个错误到待分发队列 */
    public void submit(ReviewError error) {
        pendingQueue.offer(error);
        log.info("提交错误到队列: {} (类型={})", error.getId(), error.getType());
    }

    /** 批量提交 */
    public void submitAll(List<ReviewError> errors) {
        errors.forEach(this::submit);
    }

    /** 查看当前队列大小 */
    public int pendingCount() {
        return pendingQueue.size();
    }

    /** 清空队列 (用于测试复位) */
    public void clearQueue() {
        pendingQueue.clear();
    }

    /**
     * 定时分发任务: 每 10 秒执行一次。
     * 从队列中取出所有待分发错误,逐个调用分发器。
     *
     * 你可以在这里断点观察: 每种 ErrorType 是否只被路由给了唯一的 Reviewer。
     */
    @Scheduled(fixedRate = 10_000)
    public void scanAndDispatch() {
        if (pendingQueue.isEmpty()) {
            return;
        }
        List<ReviewError> batch = new ArrayList<>();
        ReviewError e;
        while ((e = pendingQueue.poll()) != null) {
            batch.add(e);
        }
        log.info("调度器扫描: 发现 {} 个待分发错误", batch.size());
        for (ReviewError error : batch) {
            try {
                ErrorDispatcher.DispatchRecord record = dispatcher.dispatch(error);
                log.info("  已分发: {} -> 审查员 {} ({})",
                    record.errorType(), record.reviewerName(), record.reviewerId());
            } catch (Exception ex) {
                log.error("  分发失败: {} - {}", error.getId(), ex.getMessage());
            }
        }
    }
}
