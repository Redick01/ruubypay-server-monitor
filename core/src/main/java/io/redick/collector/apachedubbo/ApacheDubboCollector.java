package io.redick.collector.apachedubbo;

import io.redick.collector.AbstractCollector;
import cn.hutool.core.map.MapUtil;
import io.redick.config.TpMonitorConfig;
import io.redick.util.ReflectionUtil;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.dubbo.common.Version;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.store.DataStore;
import org.apache.dubbo.common.threadpool.manager.DefaultExecutorRepository;
import org.apache.dubbo.common.threadpool.manager.ExecutorRepository;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.server.WebServer;
import org.springframework.stereotype.Service;

/**
 * @author Redick01
 */
@Service
@Slf4j
@SuppressWarnings("all")
@ConditionalOnClass(ExecutorRepository.class)
public class ApacheDubboCollector extends AbstractCollector {

    @Resource
    private TpMonitorConfig tpMonitorConfig;

    @Override
    public void collect() {
        String currVersion = Version.getVersion();
        if (DubboVersion.compare(DubboVersion.VERSION_2_7_5, currVersion) > 0) {
            version275();
        } else {
            ExecutorRepository executorRepository;
            if (DubboVersion.compare(currVersion, DubboVersion.VERSION_3_0_3) >= 0) {
                executorRepository = ApplicationModel.defaultModel().getExtensionLoader(ExecutorRepository.class).getDefaultExtension();
            } else {
                executorRepository = ExtensionLoader.getExtensionLoader(ExecutorRepository.class).getDefaultExtension();
            }
            version300(executorRepository);
        }
    }

    private void version275() {
        DataStore dataStore = ExtensionLoader.getExtensionLoader(DataStore.class).getDefaultExtension();
        Map<String, Object> executors = dataStore.get(CommonConstants.EXECUTOR_SERVICE_COMPONENT_KEY);
        executors.forEach(this::doCollect);
    }

    private void version300(ExecutorRepository executorRepository) {
        val data = (ConcurrentMap<String, ConcurrentMap<String, ExecutorService>>) ReflectionUtil.getFieldValue(
                DefaultExecutorRepository.class, "data", executorRepository);
        if (Objects.isNull(data)) {
            return;
        }
        Map<String, ExecutorService> executorMap = data.get(CommonConstants.EXECUTOR_SERVICE_COMPONENT_KEY);
        if (MapUtil.isNotEmpty(executorMap)) {
            executorMap.forEach(this::doCollect);
        }
    }

    private void doCollect(String k, Object v) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) v;
        long taskCount = executor.getCompletedTaskCount();
        int activeCount = executor.getActiveCount();
        int poolSize = executor.getPoolSize();
        int corePoolSize = executor.getCorePoolSize();
        int maximumPoolSize = executor.getMaximumPoolSize();
        int queueSize = executor.getQueue().size();
        int remainSize = executor.getQueue().remainingCapacity();
        String content = "apache dubbo线程池：{}，ActiveCount：{}， PoolSize：{}，CorePoolSize：{}， MaximumPoolSize：{}，队列总大小：{}，" +
                "已使用大小：{}，剩余：{}，已完成任务数：{}";
        log.info(content, k, activeCount, poolSize, corePoolSize, maximumPoolSize, queueSize + remainSize,
                queueSize, remainSize, taskCount);
        doCollect(tpMonitorConfig.getApplicationName(), "apachedubbo-" + k, taskCount, activeCount, poolSize,
                corePoolSize, maximumPoolSize, queueSize, remainSize);
    }

    @Override
    public void doCollectWebServer(WebServer webServer) {

    }
}
