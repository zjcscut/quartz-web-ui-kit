package club.throwable.quartz.kit.task;

import org.junit.Test;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.HashSet;
import java.util.Set;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/4/5 11:49
 */
public class Main {

    @Test
    public void testMultiTriggerToOneJob() throws Exception{
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();
        scheduler.start();
        multiTriggerToOneJob(scheduler);
        Thread.sleep(Integer.MAX_VALUE);
    }

    private static void multiTriggerToOneJob(Scheduler scheduler) throws Exception {
        JobDetail job = JobBuilder.newJob(SimpleTask1.class)
                .withIdentity("club.throwable.quartz.kit.task.SimpleTask1", "application-1")
                .build();
        Trigger trigger1 = TriggerBuilder.newTrigger()
                .withIdentity("club.throwable.quartz.kit.task.SimpleTask1", "application-1")
                .startNow()
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(5)
                        .repeatForever())
                .build();
        Trigger trigger2 = TriggerBuilder.newTrigger()
                .withIdentity("club.throwable.quartz.kit.task.SimpleTask2", "application-1")
                .startNow()
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(10)
                        .repeatForever())
                .build();
        Set<Trigger> triggers = new HashSet<>();
        triggers.add(trigger1);
        triggers.add(trigger2);
        scheduler.scheduleJob(job, triggers, true);
    }

    private static void multiJobToOneTrigger(Scheduler scheduler) throws Exception {

    }
}
