package club.throwable.quartz.kit.task;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/4/5 11:50
 */
@Slf4j
public class SimpleTask1 implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String triggerKey = jobExecutionContext.getTrigger().getKey().toString();
        log.info("SimpleTask1执行,triggerKey:{}....", triggerKey);
    }
}
