package club.throwable.quartz.kit.support;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/4/6 13:39
 */
public interface AlarmStrategy {

    void process(ScheduleTaskInfo scheduleTaskInfo);
}
