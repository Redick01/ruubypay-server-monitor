package io.redick.spring;

import io.redick.annotation.TpMonitor;
import io.redick.registry.TpRegistry;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.NonNull;

/**
 * @author Redick01
 */
public class TpBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName)
            throws BeansException {
        if (!(bean instanceof ThreadPoolExecutor)) {
            return bean;
        }
        TpMonitor tpMonitor = ApplicationContextHolder.getInstance().findAnnotationOnBean(beanName, TpMonitor.class);
        if (null == tpMonitor) {
            return bean;
        }
        TpRegistry.refresh(StringUtils.isBlank(tpMonitor.threadPoolName()) ? beanName : tpMonitor.threadPoolName(), (ThreadPoolExecutor) bean);
        return bean;
    }
}
