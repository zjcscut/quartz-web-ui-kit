package club.throwable.quartz.kit.support;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/4/6 16:59
 */
public class AlarmScheduleTaskExecutionPostProcessor implements ScheduleTaskExecutionPostProcessor {

    @Autowired(required = false)
    private AlarmStrategy alarmStrategy;

    @Override
    public void afterTaskCompletion(ScheduleTaskInfo info) {
        Optional.ofNullable(alarmStrategy).ifPresent(strategy -> strategy.process(info));
    }
}
