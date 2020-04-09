package club.throwable.h2.example;

import club.throwable.quartz.kit.support.AlarmStrategy;
import club.throwable.quartz.kit.support.ScheduleTaskInfo;
import club.throwable.quartz.kit.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/4/9 0:53
 */
@Slf4j
@Component
public class LoggingAlarmStrategy implements AlarmStrategy {

    @Override
    public void process(ScheduleTaskInfo scheduleTaskInfo) {
        if (null != scheduleTaskInfo.getThrowable()) {
            log.error("任务执行异常,任务内容:{}", JsonUtils.X.format(scheduleTaskInfo), scheduleTaskInfo.getThrowable());
        }
    }
}
