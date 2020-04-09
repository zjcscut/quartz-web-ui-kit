package club.throwable.quartz.kit.configuration;

import club.throwable.quartz.kit.dao.ScheduleTaskDao;
import club.throwable.quartz.kit.dao.ScheduleTaskParameterDao;
import club.throwable.quartz.kit.support.DataSourceHolder;
import club.throwable.quartz.kit.support.DataSourceProvider;
import club.throwable.quartz.kit.support.QuartzAdaptableJobFactory;
import club.throwable.quartz.kit.support.QuartzWebUiKitPropertiesHolder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.*;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/4/6 12:55
 */
public class QuartzWebUiKitImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, BeanFactoryAware {

    private DefaultListableBeanFactory listableBeanFactory;
    private static final Set<Class<?>> REPOSITORY_CLASSES = new HashSet<>();

    static {
        REPOSITORY_CLASSES.add(ScheduleTaskDao.class);
        REPOSITORY_CLASSES.add(ScheduleTaskParameterDao.class);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.listableBeanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                        BeanDefinitionRegistry registry) {
        QuartzWebUiKitPropertiesProvider provider = listableBeanFactory.getBean(QuartzWebUiKitPropertiesProvider.class);
        QuartzWebUiKitPropertiesHolder.PROPS = provider.provide();
        QuartzWebUiKitProperties props = QuartzWebUiKitPropertiesHolder.PROPS;
        validateQuartzWebUiKitProperties(props);
        DataSourceHolder.DATA_SOURCE = DataSourceProvider.X.provideDataSource(QuartzDataSourceProperties.builder()
                .driverClassName(props.getDriverClassName())
                .url(props.getUrl())
                .username(props.getUsername())
                .password(props.getPassword())
                .build());
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSourceHolder.DATA_SOURCE);
        REPOSITORY_CLASSES.forEach(klass -> registerRepositoryBeanDefinition(klass, jdbcTemplate, registry));
        registerJobFactoryBeanDefinition(registry);
        registerSchedulerBeanDefinition(registry, this.listableBeanFactory.getBean(QuartzAdaptableJobFactory.class), props);

    }

    private void validateQuartzWebUiKitProperties(QuartzWebUiKitProperties props) {
        if (null == props.getUrl()) {
            throw new IllegalArgumentException("url");
        }
        if (null == props.getDriverClassName()) {
            throw new IllegalArgumentException("driverClassName");
        }
        if (null == props.getUsername()) {
            throw new IllegalArgumentException("username");
        }
        if (null == props.getPassword()) {
            throw new IllegalArgumentException("password");
        }
    }

    private void registerRepositoryBeanDefinition(Class<?> repositoryClass,
                                                  JdbcTemplate jdbcTemplate,
                                                  BeanDefinitionRegistry registry) {
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(repositoryClass)
                .addConstructorArgValue(jdbcTemplate)
                .getBeanDefinition();
        registry.registerBeanDefinition(DefaultBeanNameGenerator.INSTANCE.generateBeanName(beanDefinition, registry),
                beanDefinition);
    }

    private void registerJobFactoryBeanDefinition(BeanDefinitionRegistry registry) {
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(QuartzAdaptableJobFactory.class)
                .getBeanDefinition();
        registry.registerBeanDefinition(DefaultBeanNameGenerator.INSTANCE.generateBeanName(beanDefinition, registry),
                beanDefinition);
    }

    private void registerSchedulerBeanDefinition(BeanDefinitionRegistry registry,
                                                 QuartzAdaptableJobFactory jobFactory,
                                                 QuartzWebUiKitProperties quartzWebUiKitProperties) {
        Properties props = new Properties();
        props.put("org.quartz.scheduler.instanceName", "DefaultQuartzScheduler");
        props.put("org.quartz.scheduler.instanceId", "AUTO");
        props.put("org.quartz.scheduler.rmi.export", "false");
        props.put("org.quartz.scheduler.rmi.proxy", "false");
        props.put("org.quartz.scheduler.wrapJobExecutionInUserTransaction", "false");
        props.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        props.put("org.quartz.threadPool.threadCount", "10");
        props.put("org.quartz.threadPool.threadPriority", "5");
        props.put("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");
        props.put("org.quartz.jobStore.misfireThreshold", "60000");
        props.put("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        props.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
        props.put("org.quartz.jobStore.useProperties", "true");
        props.put("org.quartz.jobStore.tablePrefix", "QRTZ_");
        props.put("org.quartz.jobStore.isClustered", "true");
        if (null == quartzWebUiKitProperties.getPairs()
                || !quartzWebUiKitProperties.getPairs().containsKey("org.quartz.jobStore.dataSource")) {
            // 数据源配置
            props.put("org.quartz.jobStore.dataSource", "quartzDataSource");
            props.put("org.quartz.dataSource.quartzDataSource.connectionProvider.class",
                    "club.throwable.quartz.kit.support.QuartzConnectionProvider");
        }
        // 覆盖
        if (null != quartzWebUiKitProperties.getPairs()) {
            quartzWebUiKitProperties.getPairs().forEach(props::setProperty);
        }
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(SchedulerFactoryBean.class)
                .addPropertyValue("jobFactory", jobFactory)
                .addPropertyValue("quartzProperties", props)
                .getBeanDefinition();
        registry.registerBeanDefinition(DefaultBeanNameGenerator.INSTANCE.generateBeanName(beanDefinition, registry),
                beanDefinition);
    }
}
