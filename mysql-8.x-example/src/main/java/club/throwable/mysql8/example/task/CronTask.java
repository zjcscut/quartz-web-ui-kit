package club.throwable.mysql8.example.task;

import club.throwable.quartz.kit.support.AbstractScheduleTask;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/4/6 23:55
 */
@Slf4j
public class CronTask extends AbstractScheduleTask {

    @Override
    protected void executeInternal(JobExecutionContext context) {
        logger.info("CronTask触发,TriggerKey:{}", context.getTrigger().getKey().toString());
    }
}
