package club.throwable.quartz.kit.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/3/27 19:08
 */
@RequiredArgsConstructor
@Getter
public enum ScheduleTaskType {

    CRON("CRON", "cron表达式任务"),

    SIMPLE("SIMPLE", "周期性执行任务"),

    ;

    private final String type;
    private final String message;

    public static ScheduleTaskType fromType(String type) {
        for (ScheduleTaskType scheduleTaskType : ScheduleTaskType.values()) {
            if (scheduleTaskType.getType().equalsIgnoreCase(type)) {
                return scheduleTaskType;
            }
        }
        throw new IllegalArgumentException("type = " + type);
    }
}
