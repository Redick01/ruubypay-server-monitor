package io.redick.collector.customize;

import io.redick.collector.AbstractCollector;
import io.redick.config.TpMonitorConfig;
import io.redick.registry.TpRegistry;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.server.WebServer;
import org.springframework.stereotype.Service;

/**
 * @author Redick01
 */
@Service
@Slf4j
public class CustomizeCollector extends AbstractCollector {

    @Resource
    private TpMonitorConfig tpMonitorConfig;

    @Override
    public void collect() {
        Map<String, ThreadPoolExecutor> executorMap = TpRegistry.TP_REGISTRY;
        executorMap.forEach((k, v) -> {
            long taskCount = v.getCompletedTaskCount();
            int activeCount = v.getActiveCount();
            int poolSize = v.getPoolSize();
            int corePoolSize = v.getCorePoolSize();
            int maximumPoolSize = v.getMaximumPoolSize();
            int queueSize = v.getQueue().size();
            int remainSize = v.getQueue().remainingCapacity();
            String content = "自定义线程池：{}，ActiveCount：{}， PoolSize：{}，CorePoolSize：{}， MaximumPoolSize：{}，队列总大小：{}，" +
                    "已使用大小：{}，剩余：{}，已完成任务数：{}";
            log.info(content, k, activeCount, poolSize, corePoolSize, maximumPoolSize, queueSize + remainSize,
                    queueSize, remainSize, taskCount);
            doCollect(tpMonitorConfig.getApplicationName(), "customize-" + k, taskCount, activeCount, poolSize,
                    corePoolSize, maximumPoolSize, queueSize, remainSize);
        });
    }

    @Override
    public void doCollectWebServer(WebServer webServer) {

    }
}
