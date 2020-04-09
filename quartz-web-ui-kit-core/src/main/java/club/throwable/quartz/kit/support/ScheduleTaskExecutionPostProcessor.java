package club.throwable.quartz.kit.support;

import club.throwable.quartz.kit.common.ScheduleTaskExecutionStatus;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/4/6 13:41
 */
public interface ScheduleTaskExecutionPostProcessor {

    default void beforeTaskExecution(ScheduleTaskInfo info) {

    }

    default void afterTaskExecution(ScheduleTaskInfo info, ScheduleTaskExecutionStatus status) {

    }

    default void afterTaskCompletion(ScheduleTaskInfo info) {

    }
}
