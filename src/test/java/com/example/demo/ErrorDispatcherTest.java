package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 错误分发系统测试 —— 核心验证: 每种 ErrorType 只分配给一个审查员。
 */
@DisplayName("错误分发系统")
class ErrorDispatcherTest {

    private ReviewerAssignmentService assignmentService;
    private ErrorDispatcher dispatcher;
    private ErrorScheduler scheduler;

    @BeforeEach
    void setUp() {
        assignmentService = new ReviewerAssignmentService();
        assignmentService.initDefaults();
        dispatcher = new ErrorDispatcher(assignmentService);
        scheduler = new ErrorScheduler(dispatcher);
    }

    // ==================================================== 1:1 映射约束

    @Nested
    @DisplayName("1:1 映射约束 — 每种 ErrorType 只能有一个审查员")
    class OneToOneMapping {

        @Test
        @DisplayName("相同 ErrorType 分配给同一审查员不会抛异常")
        void sameReviewerSameTypeShouldNotThrow() {
            Reviewer r1a = new Reviewer("R001", "张三", ErrorType.NULL_POINTER);
            Reviewer r1b = new Reviewer("R001", "张三", ErrorType.NULL_POINTER);
            assignmentService.assign(ErrorType.NULL_POINTER, r1a);
            assertDoesNotThrow(() ->
                assignmentService.assign(ErrorType.NULL_POINTER, r1b));
        }

        @Test
        @DisplayName("不同审查员抢同一个 ErrorType 必须抛异常")
        void differentReviewerSameTypeShouldThrow() {
            assignmentService.assign(ErrorType.NULL_POINTER,
                new Reviewer("R001", "张三", ErrorType.NULL_POINTER));

            IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                assignmentService.assign(ErrorType.NULL_POINTER,
                    new Reviewer("R999", "闯入者", ErrorType.NULL_POINTER)));

            assertTrue(ex.getMessage().contains("NULL_POINTER"));
            assertTrue(ex.getMessage().contains("张三"));
            assertTrue(ex.getMessage().contains("闯入者"));
        }

        @Test
        @DisplayName("initDefaults 后 6 种逻辑错误各有唯一审查员")
        void initDefaultsShouldCreateOneToOneMapping() {
            var assignments = assignmentService.getAllAssignments();
            assertEquals(6, assignments.size());
            for (ErrorType type : ErrorType.values()) {
                assertTrue(assignmentService.getReviewer(type).isPresent(),
                    type + " 应该有审查员");
            }
        }
    }

    // ==================================================== 分发正确性

    @Nested
    @DisplayName("分发正确性")
    class DispatchCorrectness {

        @Test
        @DisplayName("6 种逻辑错误各自分发到唯一审查员,无交叉")
        void allLogicTypesGoToTheirUniqueReviewers() {
            List<ReviewError> errors = List.of(
                new ReviewError(ErrorType.NULL_POINTER, "e1"),
                new ReviewError(ErrorType.OFF_BY_ONE, "e2"),
                new ReviewError(ErrorType.RACE_CONDITION, "e3"),
                new ReviewError(ErrorType.DEAD_LOCK, "e4"),
                new ReviewError(ErrorType.INFINITE_RECURSION, "e5"),
                new ReviewError(ErrorType.WRONG_COMPARATOR, "e6")
            );

            List<ErrorDispatcher.DispatchRecord> records = dispatcher.dispatchAll(errors);
            assertEquals(6, records.size());

            for (var r : records) {
                Reviewer assigned = assignmentService.getReviewer(r.errorType()).orElseThrow();
                assertEquals(assigned.getId(), r.reviewerId(),
                    r.errorType() + " 应该分给 " + assigned.getName());
            }
        }

        @Test
        @DisplayName("同一逻辑错误类型多次提交,全部给同一个审查员")
        void sameTypeAllGoToSameReviewer() {
            List<ReviewError> errors = List.of(
                new ReviewError(ErrorType.RACE_CONDITION, "e1"),
                new ReviewError(ErrorType.RACE_CONDITION, "e2"),
                new ReviewError(ErrorType.RACE_CONDITION, "e3")
            );

            List<ErrorDispatcher.DispatchRecord> records = dispatcher.dispatchAll(errors);
            assertEquals(3, records.size());

            for (var r : records) {
                assertEquals("R003", r.reviewerId());
                assertEquals("王五", r.reviewerName());
            }
        }

        @Test
        @DisplayName("dispatch 方法对未分配审查员的类型抛异常")
        void unassignedTypeShouldThrow() {
            var emptyAssignment = new ReviewerAssignmentService();
            var emptyDispatcher = new ErrorDispatcher(emptyAssignment);
            ReviewError error = new ReviewError(ErrorType.NULL_POINTER, "无人认领");

            assertThrows(IllegalStateException.class, () ->
                emptyDispatcher.dispatch(error));
        }
    }

    // ==================================================== 调度器

    @Nested
    @DisplayName("调度器流程")
    class SchedulerFlow {

        @Test
        @DisplayName("提交 → 调度 → 每个逻辑错误分发到唯一审查员")
        void submitAndDispatchEachToOneReviewer() {
            scheduler.submit(new ReviewError(ErrorType.NULL_POINTER, "e1"));
            scheduler.submit(new ReviewError(ErrorType.DEAD_LOCK, "e2"));
            scheduler.submit(new ReviewError(ErrorType.NULL_POINTER, "e3"));

            assertEquals(3, scheduler.pendingCount());
            scheduler.scanAndDispatch();
            assertEquals(0, scheduler.pendingCount());

            List<ErrorDispatcher.DispatchRecord> history = dispatcher.getHistory();
            assertEquals(3, history.size());

            for (var r : history) {
                if (r.errorType() == ErrorType.NULL_POINTER) {
                    assertEquals("R001", r.reviewerId());
                }
                if (r.errorType() == ErrorType.DEAD_LOCK) {
                    assertEquals("R004", r.reviewerId());
                }
            }
        }
    }
}
