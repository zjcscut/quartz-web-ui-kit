package club.throwable.quartz.kit.configuration;

import club.throwable.quartz.kit.support.AlarmScheduleTaskExecutionPostProcessor;
import club.throwable.quartz.kit.support.AlarmStrategy;
import club.throwable.quartz.kit.support.NoneAlarmStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/4/6 12:46
 */
@ComponentScan(basePackages = {
        "club.throwable.quartz.kit.controller",
        "club.throwable.quartz.kit.service"
})
@Configuration
@Import(QuartzWebUiKitImportBeanDefinitionRegistrar.class)
public class QuartzWebUiKitConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AlarmStrategy alarmStrategy() {
        return new NoneAlarmStrategy();
    }

    @Bean
    public AlarmScheduleTaskExecutionPostProcessor alarmScheduleTaskExecutionPostProcessor() {
        return new AlarmScheduleTaskExecutionPostProcessor();
    }
}
