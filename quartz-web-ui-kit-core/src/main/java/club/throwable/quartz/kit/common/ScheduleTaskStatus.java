package club.throwable.quartz.kit.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/3/27 18:56
 */
@RequiredArgsConstructor
@Getter
public enum ScheduleTaskStatus {

    ONLINE(1, "启动", "在线 - 启动状态"),

    OFFLINE(0, "停止", "下线 - 停止状态"),

    ;

    private final Integer status;
    private final String shortMessage;
    private final String message;

    public static ScheduleTaskStatus fromType(Integer status) {
        for (ScheduleTaskStatus scheduleTaskStatus : ScheduleTaskStatus.values()) {
            if (scheduleTaskStatus.getStatus().equals(status)) {
                return scheduleTaskStatus;
            }
        }
        throw new IllegalArgumentException("status = " + status);
    }
}
