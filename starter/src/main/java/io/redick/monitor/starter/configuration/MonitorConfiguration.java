package io.redick.monitor.starter.configuration;

import io.redick.MonitorRun;
import io.redick.config.TpMonitorConfig;
import io.redick.spring.ApplicationContextHolder;
import io.redick.spring.TpBeanPostProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Redick01
 */
@Configuration
@EnableConfigurationProperties(TpMonitorConfig.class)
@ComponentScan("io.redick.collector")
public class MonitorConfiguration {

    @Bean
    public MonitorRun monitorRun() {
        return new MonitorRun();
    }

    @Bean
    public TpBeanPostProcessor tpBeanPostProcessor() {
        return new TpBeanPostProcessor();
    }

    @Bean
    public ApplicationContextHolder applicationContextHolder() {
        return new ApplicationContextHolder();
    }
}
