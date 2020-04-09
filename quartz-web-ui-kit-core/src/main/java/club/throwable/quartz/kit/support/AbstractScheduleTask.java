package club.throwable.quartz.kit.support;

import club.throwable.quartz.kit.common.ScheduleTaskExecutionStatus;
import club.throwable.quartz.kit.domain.ScheduleTask;
import club.throwable.quartz.kit.utils.JsonUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/4/6 13:47
 */
@DisallowConcurrentExecution
public abstract class AbstractScheduleTask implements Job {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    private List<ScheduleTaskExecutionPostProcessor> processors;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String scheduleTask = context.getMergedJobDataMap().getString("scheduleTask");
        ScheduleTask task = JsonUtils.X.parse(scheduleTask, ScheduleTask.class);
        ScheduleTaskInfo info = ScheduleTaskInfo.builder()
                .taskId(task.getTaskId())
                .taskClass(task.getTaskClass())
                .taskDescription(task.getTaskDescription())
                .taskExpression(task.getTaskExpression())
                .taskGroup(task.getTaskGroup())
                .taskType(task.getTaskType())
                .build();
        long start = System.currentTimeMillis();
        info.setStart(start);
        // 在MDC中添加traceId便于追踪调用链
        MappedDiagnosticContextAssistant.X.processInMappedDiagnosticContext(() -> {
            try {
                if (enableLogging()) {
                    logger.info("任务[{}]-[{}]-[{}]开始执行......", task.getTaskId(), task.getTaskClass(), task.getTaskDescription());
                }
                // 执行前的处理器回调
                processBeforeTaskExecution(info);
                // 子类实现的任务执行逻辑
                executeInternal(context);
                // 执行成功的处理器回调
                processAfterTaskExecution(info, ScheduleTaskExecutionStatus.SUCCESS);
            } catch (Exception e) {
                info.setThrowable(e);
                if (enableLogging()) {
                    logger.info("任务[{}]-[{}]-[{}]执行异常", task.getTaskId(), task.getTaskClass(),
                            task.getTaskDescription(), e);
                }
                // 执行异常的处理器回调
                processAfterTaskExecution(info, ScheduleTaskExecutionStatus.FAIL);
            } finally {
                long end = System.currentTimeMillis();
                long cost = end - start;
                info.setEnd(end);
                info.setCost(cost);
                if (enableLogging() && null != info.getThrowable()) {
                    logger.info("任务[{}]-[{}]-[{}]执行完毕,耗时:{} ms......", task.getTaskId(), task.getTaskClass(),
                            task.getTaskDescription(), cost);
                }
                // 执行结束的处理器回调
                processAfterTaskCompletion(info);
            }
        });
    }

    protected boolean enableLogging() {
        return true;
    }

    /**
     * 内部执行方法 - 子类实现
     *
     * @param context context
     */
    protected abstract void executeInternal(JobExecutionContext context);

    /**
     * 拷贝任务信息
     */
    private ScheduleTaskInfo copyScheduleTaskInfo(ScheduleTaskInfo info) {
        return ScheduleTaskInfo.builder()
                .cost(info.getCost())
                .start(info.getStart())
                .end(info.getEnd())
                .throwable(info.getThrowable())
                .taskId(info.getTaskId())
                .taskClass(info.getTaskClass())
                .taskDescription(info.getTaskDescription())
                .taskExpression(info.getTaskExpression())
                .taskGroup(info.getTaskGroup())
                .taskType(info.getTaskType())
                .build();
    }

    void processBeforeTaskExecution(ScheduleTaskInfo info) {
        if (null != processors) {
            for (ScheduleTaskExecutionPostProcessor processor : processors) {
                processor.beforeTaskExecution(copyScheduleTaskInfo(info));
            }
        }
    }

    void processAfterTaskExecution(ScheduleTaskInfo info, ScheduleTaskExecutionStatus status) {
        if (null != processors) {
            for (ScheduleTaskExecutionPostProcessor processor : processors) {
                processor.afterTaskExecution(copyScheduleTaskInfo(info), status);
            }
        }
    }

    void processAfterTaskCompletion(ScheduleTaskInfo info) {
        if (null != processors) {
            for (ScheduleTaskExecutionPostProcessor processor : processors) {
                processor.afterTaskCompletion(copyScheduleTaskInfo(info));
            }
        }
    }
}
