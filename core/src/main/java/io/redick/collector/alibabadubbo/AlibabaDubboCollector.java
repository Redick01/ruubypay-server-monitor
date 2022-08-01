package io.redick.collector.alibabadubbo;

import static com.alibaba.dubbo.common.Constants.EXECUTOR_SERVICE_COMPONENT_KEY;

import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.common.store.DataStore;
import io.redick.collector.AbstractCollector;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Redick01
 */
@Slf4j
public class AlibabaDubboCollector extends AbstractCollector {

    private final String applicationName;

    public AlibabaDubboCollector(String applicationName) {
        this.applicationName = applicationName;
    }

    @Override
    public void collect() {
        DataStore dataStore = ExtensionLoader.getExtensionLoader(DataStore.class).getDefaultExtension();
        Map<String, Object> executors = dataStore.get(EXECUTOR_SERVICE_COMPONENT_KEY);
        executors.forEach((k, v) -> {
            ThreadPoolExecutor executor = (ThreadPoolExecutor) v;
            long taskCount = executor.getCompletedTaskCount();
            int activeCount = executor.getActiveCount();
            int poolSize = executor.getPoolSize();
            int corePoolSize = executor.getCorePoolSize();
            int maximumPoolSize = executor.getMaximumPoolSize();
            int queueSize = executor.getQueue().size();
            int remainSize = executor.getQueue().remainingCapacity();
            String content = "dubbo线程池：{}，ActiveCount：{}， PoolSize：{}，CorePoolSize：{}， MaximumPoolSize：{}，队列总大小：{}，" +
                    "已使用大小：{}，剩余：{}，已完成任务数：{}";
            log.info(content, k, activeCount, poolSize, corePoolSize, maximumPoolSize, queueSize + remainSize,
                    queueSize, remainSize, taskCount);
            doCollect(applicationName, "alibabadubbo-" + k, taskCount, activeCount, poolSize,
                    corePoolSize, maximumPoolSize, queueSize, remainSize);
        });
    }
}
