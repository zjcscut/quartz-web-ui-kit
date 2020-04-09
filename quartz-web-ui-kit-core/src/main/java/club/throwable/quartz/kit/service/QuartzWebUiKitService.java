package club.throwable.quartz.kit.service;

import club.throwable.quartz.kit.common.ScheduleTaskStatus;
import club.throwable.quartz.kit.common.ScheduleTaskType;
import club.throwable.quartz.kit.dao.ScheduleTaskDao;
import club.throwable.quartz.kit.dao.ScheduleTaskParameterDao;
import club.throwable.quartz.kit.domain.ScheduleTask;
import club.throwable.quartz.kit.domain.ScheduleTaskParameter;
import club.throwable.quartz.kit.exception.QuartzWebUiKitException;
import club.throwable.quartz.kit.service.vo.AddScheduleTaskVo;
import club.throwable.quartz.kit.service.vo.EditScheduleTaskVo;
import club.throwable.quartz.kit.service.vo.ScheduleTaskInfoVo;
import club.throwable.quartz.kit.support.DataSourceHolder;
import club.throwable.quartz.kit.utils.BeanCopierUtils;
import club.throwable.quartz.kit.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/4/6 12:50
 */
@Slf4j
@Service
public class QuartzWebUiKitService implements SmartInitializingSingleton, InitializingBean {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private ScheduleTaskParameterDao scheduleTaskParameterDao;

    @Autowired
    private ScheduleTaskDao scheduleTaskDao;

    @Value("${spring.application.name:DEFAULT}")
    private String taskGroup;

    ExecutorService executor;
    TransactionTemplate transactionTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        executor = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable, "QuartzWebUiKitWorker");
            thread.setDaemon(true);
            return thread;
        });
        transactionTemplate = new TransactionTemplate(new DataSourceTransactionManager(DataSourceHolder.DATA_SOURCE));
    }

    @Override
    public void afterSingletonsInstantiated() {
        executor.execute(() -> {
            List<ScheduleTask> scheduleTasks = scheduleTaskDao.selectTasksByStatus(getTaskGroup(), null);
            if (!scheduleTasks.isEmpty()) {
                for (ScheduleTask task : scheduleTasks) {
                    scheduleTaskInternal(task);
                }
            }
        });
    }

    private void scheduleTaskInternal(ScheduleTask task) {
        try {
            checkTaskClass(task.getTaskClass());
            ScheduleTaskType scheduleTaskType = ScheduleTaskType.fromType(task.getTaskType());
            if (scheduleTaskType == ScheduleTaskType.CRON) {
                scheduleCronTask(task);
            }
            if (scheduleTaskType == ScheduleTaskType.SIMPLE) {
                scheduleSimpleTask(task);
            }
            log.info("装载任务[{}]-[{}]-[{}]-[{}]到Quartz调度器成功", task.getTaskId(), task.getTaskClass(),
                    task.getTaskDescription(), task.getTaskExpression());
        } catch (Exception e) {
            log.error("加载调度任务失败,任务信息:{}", JsonUtils.X.format(task), e);
        }
    }

    private void scheduleCronTask(ScheduleTask task) throws Exception {
        TriggerKey triggerKey = TriggerKey.triggerKey(task.getTaskClass(), task.getTaskGroup());
        Trigger trigger = scheduler.getTrigger(triggerKey);
        ScheduleTaskExpression expression = extractScheduleTaskExpression(task.getTaskExpression());
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder
                .cronSchedule(expression.getCron())
                // 错失触发什么也不做 - 这是参考实践中遇到的问题,不一定所有的场景都适合
                .withMisfireHandlingInstructionDoNothing();
        CronTrigger newCronTrigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .withSchedule(cronScheduleBuilder)
                .startAt(new Date(expression.getStartAt()))
                .build();
        refreshScheduleTask(task, trigger, triggerKey, newCronTrigger);
    }

    private void scheduleSimpleTask(ScheduleTask task) throws Exception {
        TriggerKey triggerKey = TriggerKey.triggerKey(task.getTaskClass(), task.getTaskGroup());
        Trigger trigger = scheduler.getTrigger(triggerKey);
        ScheduleTaskExpression expression = extractScheduleTaskExpression(task.getTaskExpression());
        SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInMilliseconds(expression.getIntervalInMilliseconds())
                .withRepeatCount(expression.getRepeatCount());
        SimpleTrigger newSimpleTrigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .withSchedule(simpleScheduleBuilder)
                .startAt(new Date(expression.getStartAt()))
                .build();
        refreshScheduleTask(task, trigger, triggerKey, newSimpleTrigger);
    }

    @SuppressWarnings("unchecked")
    public void refreshScheduleTask(ScheduleTask task,
                                    Trigger oldTrigger,
                                    TriggerKey triggerKey,
                                    Trigger newTrigger) throws Exception {
        JobDataMap jobDataMap = prepareJobDataMap(task);
        JobDetail jobDetail =
                JobBuilder.newJob((Class<? extends Job>) Class.forName(task.getTaskClass()))
                        .withIdentity(task.getTaskClass(), task.getTaskGroup())
                        .usingJobData(jobDataMap)
                        .build();
        // 总是覆盖
        if (ScheduleTaskStatus.ONLINE == ScheduleTaskStatus.fromType(task.getTaskStatus())) {
            scheduler.scheduleJob(jobDetail, Collections.singleton(newTrigger), Boolean.TRUE);
        } else {
            if (null != oldTrigger) {
                scheduler.unscheduleJob(triggerKey);
            }
        }
    }

    private static ScheduleTaskExpression extractScheduleTaskExpression(String exp) {
        ScheduleTaskExpression expression = new ScheduleTaskExpression();
        String[] split = exp.trim().split(",");
        for (String item : split) {
            String[] kv = item.split("=");
            String key = kv[0].trim();
            String value = kv[1].trim();
            if ("intervalInMilliseconds".equalsIgnoreCase(key)) {
                expression.setIntervalInMilliseconds(Long.parseLong(value));
            }
            if ("cron".equalsIgnoreCase(key)) {
                expression.setCron(value);
            }
            if ("repeatCount".equalsIgnoreCase(key)) {
                expression.setRepeatCount(Integer.valueOf(value));
            }
        }
        if (null == expression.getStartAt()) {
            expression.setStartAt(System.currentTimeMillis());
        }
        if (null == expression.getRepeatCount()) {
            expression.setRepeatCount(Integer.MAX_VALUE);
        }
        if (null == expression.getCron() && null == expression.getIntervalInMilliseconds()) {
            throw new QuartzWebUiKitException("解析任务表达式异常,exp = " + exp);
        }
        return expression;
    }

    @Data
    private static class ScheduleTaskExpression {

        private Long intervalInMilliseconds;
        private Long startAt;
        private String cron;
        private Integer repeatCount;
    }

    public List<ScheduleTask> getAllTasks() {
        return scheduleTaskDao.selectTasksByStatus(getTaskGroup(), null);
    }

    public void triggerByTaskId(String taskId) throws Exception {
        ScheduleTask task = scheduleTaskDao.selectByTaskId(taskId);
        if (null != task) {
            JobDataMap jobDataMap = prepareJobDataMap(task);
            JobKey jobKey = JobKey.jobKey(task.getTaskClass(), task.getTaskGroup());
            scheduler.triggerJob(jobKey, jobDataMap);
        }
    }

    public void stopByTaskId(String taskId) throws Exception {
        ScheduleTask task = scheduleTaskDao.selectByTaskId(taskId);
        if (null != task && ScheduleTaskStatus.ONLINE == ScheduleTaskStatus.fromType(task.getTaskStatus())) {
            ScheduleTask updater = new ScheduleTask();
            task.setTaskStatus(ScheduleTaskStatus.OFFLINE.getStatus());
            updater.setTaskStatus(task.getTaskStatus());
            updater.setId(task.getId());
            updater.setVersion(task.getVersion() + 1);
            scheduleTaskDao.updateByPrimaryKeySelective(updater);
            scheduleTaskInternal(task);
        }
    }

    public void startByTaskId(String taskId) {
        ScheduleTask task = scheduleTaskDao.selectByTaskId(taskId);
        if (null != task && ScheduleTaskStatus.OFFLINE == ScheduleTaskStatus.fromType(task.getTaskStatus())) {
            ScheduleTask updater = new ScheduleTask();
            task.setTaskStatus(ScheduleTaskStatus.ONLINE.getStatus());
            updater.setTaskStatus(task.getTaskStatus());
            updater.setId(task.getId());
            updater.setVersion(task.getVersion() + 1);
            scheduleTaskDao.updateByPrimaryKeySelective(updater);
            scheduleTaskInternal(task);
        }
    }

    public ScheduleTaskInfoVo selectByTaskId(String taskId) {
        ScheduleTask task = scheduleTaskDao.selectByTaskId(taskId);
        if (null != task) {
            ScheduleTaskInfoVo info = new ScheduleTaskInfoVo();
            BeanCopierUtils.X.copy(task, info);
            ScheduleTaskParameter parameter = scheduleTaskParameterDao.selectByTaskId(taskId);
            if (null != parameter) {
                info.setTaskParameter(parameter.getParameterValue());
            }
            return info;
        }
        return null;
    }

    public void editTask(EditScheduleTaskVo vo) {
        if (StringUtils.hasLength(vo.getTaskParameter())) {
            checkTaskParameter(vo.getTaskParameter());
        }
        ScheduleTask task = scheduleTaskDao.selectByTaskId(vo.getTaskId());
        if (null != task) {
            ScheduleTask updater = new ScheduleTask();
            BeanCopierUtils.X.copy(vo, updater);
            task.setTaskDescription(vo.getTaskDescription());
            task.setTaskStatus(vo.getTaskStatus());
            task.setTaskExpression(vo.getTaskExpression());
            updater.setId(task.getId());
            updater.setVersion(task.getVersion() + 1);
            ScheduleTaskParameter parameter = scheduleTaskParameterDao.selectByTaskId(vo.getTaskId());
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                    scheduleTaskDao.updateByPrimaryKeySelective(updater);
                    if (StringUtils.hasLength(vo.getTaskParameter())) {
                        if (null != parameter) {
                            parameter.setParameterValue(vo.getTaskParameter());
                            scheduleTaskParameterDao.updateByPrimaryKeySelective(parameter);
                        } else {
                            ScheduleTaskParameter taskParameter = new ScheduleTaskParameter();
                            taskParameter.setTaskId(task.getTaskId());
                            taskParameter.setParameterValue(vo.getTaskParameter());
                            scheduleTaskParameterDao.insertSelective(taskParameter);
                        }
                    }
                }
            });
            scheduleTaskInternal(task);
        }
    }

    public void addTask(AddScheduleTaskVo vo) {
        String taskGroup = getTaskGroup();
        ScheduleTask task = scheduleTaskDao.selectByTaskClassAndGroup(vo.getTaskClass(), taskGroup);
        if (null != task) {
            throw new QuartzWebUiKitException(String.format("任务类:%s,分组:%s已经存在", vo.getTaskClass(), taskGroup));
        }
        checkTaskClass(vo.getTaskClass());
        if (StringUtils.hasLength(vo.getTaskParameter())) {
            checkTaskParameter(vo.getTaskParameter());
        }
        ScheduleTask newTask = new ScheduleTask();
        BeanCopierUtils.X.copy(vo, newTask);
        newTask.setTaskId(UUID.randomUUID().toString());
        if (null != vo.getTaskExpression() && vo.getTaskExpression().contains("intervalInMilliseconds")) {
            newTask.setTaskType(ScheduleTaskType.SIMPLE.getType());
        } else if (null != vo.getTaskExpression() && vo.getTaskExpression().contains("cron")) {
            newTask.setTaskType(ScheduleTaskType.CRON.getType());
        }
        if (null == newTask.getTaskType()) {
            throw new IllegalArgumentException("任务表达式异常,解析taskType异常");
        }
        newTask.setTaskGroup(taskGroup);
        newTask.setDeleted(0);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                scheduleTaskDao.insertSelective(newTask);
                if (StringUtils.hasLength(vo.getTaskParameter())) {
                    ScheduleTaskParameter parameter = new ScheduleTaskParameter();
                    parameter.setTaskId(newTask.getTaskId());
                    parameter.setParameterValue(vo.getTaskParameter());
                    scheduleTaskParameterDao.insertSelective(parameter);
                }
            }
        });
        scheduleTaskInternal(newTask);
    }

    public void deleteTaskByTaskId(String taskId) throws Exception {
        ScheduleTask task = scheduleTaskDao.selectByTaskId(taskId);
        if (null != task) {
            TriggerKey triggerKey = TriggerKey.triggerKey(task.getTaskClass(), task.getTaskGroup());
            JobKey jobKey = JobKey.jobKey(task.getTaskClass(), task.getTaskGroup());
            scheduler.unscheduleJob(triggerKey);
            scheduler.deleteJob(jobKey);
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                    scheduleTaskDao.deleteByTaskId(taskId);
                    scheduleTaskParameterDao.deleteByTaskId(taskId);
                }
            });
        }
    }

    private JobDataMap prepareJobDataMap(ScheduleTask task) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("scheduleTask", JsonUtils.X.format(task));
        ScheduleTaskParameter taskParameter = scheduleTaskParameterDao.selectByTaskId(task.getTaskId());
        if (null != taskParameter) {
            Map<String, Object> parameterMap = JsonUtils.X.parse(taskParameter.getParameterValue(),
                    new TypeReference<Map<String, Object>>() {
                    });
            jobDataMap.putAll(parameterMap);
        }
        return jobDataMap;
    }

    public String getTaskGroup() {
        return taskGroup;
    }

    private void checkTaskClass(String taskClass) {
        try {
            ClassUtils.forName(taskClass, null);
        } catch (ClassNotFoundException e) {
            throw new QuartzWebUiKitException(String.format("任务类:%s不存在", taskClass));
        }
    }

    private void checkTaskParameter(String taskParameter) {
        try {
            JsonUtils.X.parse(taskParameter, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            throw new QuartzWebUiKitException(String.format("解析任务参数为JSON异常,taskParameter:%s", taskParameter));
        }
    }
}
