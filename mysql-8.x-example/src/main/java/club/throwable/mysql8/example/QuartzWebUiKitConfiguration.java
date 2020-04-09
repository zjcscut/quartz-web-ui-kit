package club.throwable.mysql8.example;

import club.throwable.quartz.kit.configuration.QuartzWebUiKitProperties;
import club.throwable.quartz.kit.configuration.QuartzWebUiKitPropertiesProvider;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/4/7 0:03
 */
@Configuration
public class QuartzWebUiKitConfiguration implements EnvironmentAware {

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public QuartzWebUiKitPropertiesProvider quartzWebUiKitPropertiesProvider() {
        return () -> {
            QuartzWebUiKitProperties properties = new QuartzWebUiKitProperties();
            properties.setDriverClassName(environment.getProperty("spring.datasource.driver-class-name"));
            properties.setUrl(environment.getProperty("spring.datasource.url"));
            properties.setUsername(environment.getProperty("spring.datasource.username"));
            properties.setPassword(environment.getProperty("spring.datasource.password"));
            return properties;
        };
    }
}
