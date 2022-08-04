package io.redick.collector;

import com.google.common.collect.Lists;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import io.redick.spring.ApplicationContextHolder;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.ApplicationContext;

/**
 * @author Redick01
 */
public abstract class AbstractCollector implements Collector {

    public static final String METRIC_NAME_PREFIX = "tp.monitor";

    public static final String POOL_NAME_TAG = METRIC_NAME_PREFIX + ".key";

    public static final String APP_NAME_TAG = "app.name";

    public static final String TP_NAME = "tp.name";

    public void doCollect(String appName, String tpName, long completedTaskCount, int activeCount, int poolSize,
            int corePoolSize, int maximumPoolSize, int queueSize, int remainSize) {
        Iterable<Tag> tags = Lists.newArrayList(
                Tag.of(POOL_NAME_TAG, tpName),
                Tag.of(APP_NAME_TAG, appName),
                Tag.of(TP_NAME, tpName));
        Metrics.gauge(metricName("completed.task.count"), tags, completedTaskCount);
        Metrics.gauge(metricName("active.count"), tags, activeCount);
        Metrics.gauge(metricName("pool.size"), tags, poolSize);
        Metrics.gauge(metricName("core.pool.size"), tags, corePoolSize);
        Metrics.gauge(metricName("maximum.pool.size"), tags, maximumPoolSize);
        Metrics.gauge(metricName("queue.size"), tags, queueSize);
        Metrics.gauge(metricName("queue.reamin.size"), tags, remainSize);
    }

    /**
     * get metric name.
     * @param name name
     * @return metric name
     */
    private static String metricName(String name) {
        return String.join(".", METRIC_NAME_PREFIX, name);
    }

    public void collectWebServer() {
        ApplicationContext applicationContext = ApplicationContextHolder.getInstance();
        WebServer webServer = ((WebServerApplicationContext) applicationContext).getWebServer();
        doCollectWebServer(webServer);
    }

    /**
     * collect web server.
     */
    public abstract void doCollectWebServer(WebServer webServer);
}
