package io.redick;

import io.redick.collector.Collector;
import io.redick.config.TpMonitorConfig;
import io.redick.thread.NamedThreadFactory;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;

/**
 * @author Redick01
 */
public class MonitorRun implements ApplicationListener<ServletWebServerInitializedEvent> {

    private static final ScheduledExecutorService MONITOR_EXECUTOR = new ScheduledThreadPoolExecutor(
            1, new NamedThreadFactory("tp-monitor"));

    @Resource
    private TpMonitorConfig tpMonitorConfig;

    @Resource
    private List<Collector> collectors;

    @Override
    public void onApplicationEvent(@NonNull ServletWebServerInitializedEvent event) {
        MONITOR_EXECUTOR.scheduleWithFixedDelay(this::monitor,
                0, tpMonitorConfig.getMonitorInterval(), TimeUnit.SECONDS);
    }

    public void monitor() {
        collectors.forEach(Collector::collect);
    }
}
