package io.redick;

import io.micrometer.core.instrument.util.NamedThreadFactory;
import io.redick.collector.Collector;
import io.redick.config.TpMonitorConfig;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;

/**
 * @author Redick01
 */
public class MonitorRun implements ApplicationRunner, Ordered {

    private static final ScheduledExecutorService MONITOR_EXECUTOR = new ScheduledThreadPoolExecutor(
            1, new NamedThreadFactory("tp-monitor"));

    @Resource
    private TpMonitorConfig tpMonitorConfig;

    @Resource
    private List<Collector> collectors;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        MONITOR_EXECUTOR.scheduleWithFixedDelay(this::monitor,
                0, tpMonitorConfig.getMonitorInterval(), TimeUnit.SECONDS);
    }

    public void monitor() {
        collectors.forEach(Collector::collect);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
