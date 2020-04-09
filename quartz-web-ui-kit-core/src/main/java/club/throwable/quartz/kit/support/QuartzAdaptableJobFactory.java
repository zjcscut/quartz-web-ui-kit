package club.throwable.quartz.kit.support;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.AdaptableJobFactory;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/3/27 17:37
 */
public class QuartzAdaptableJobFactory extends AdaptableJobFactory implements BeanFactoryAware {

    private AutowireCapableBeanFactory autowireCapableBeanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.autowireCapableBeanFactory = (AutowireCapableBeanFactory) beanFactory;
    }

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        Object jobInstance = super.createJobInstance(bundle);
        this.autowireCapableBeanFactory.autowireBean(jobInstance);
        return jobInstance;
    }
}
