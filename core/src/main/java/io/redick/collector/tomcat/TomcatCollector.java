package io.redick.collector.tomcat;

import io.redick.collector.AbstractCollector;
import io.redick.config.TpMonitorConfig;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.server.WebServer;
import org.springframework.stereotype.Service;

/**
 * @author Redick01
 */
@Service
@Slf4j
public class TomcatCollector extends AbstractCollector {

    @Resource
    private TpMonitorConfig tpMonitorConfig;

    @Override
    public void collect() {
        collectWebServer();
    }

    @Override
    public void doCollectWebServer(WebServer webServer) {
        TomcatWebServer tomcatWebServer = (TomcatWebServer) webServer;
        ThreadPoolExecutor executor = (ThreadPoolExecutor) tomcatWebServer
                .getTomcat()
                .getConnector()
                .getProtocolHandler()
                .getExecutor();
        int port = tomcatWebServer.getPort();
        long taskCount = executor.getCompletedTaskCount();
        int activeCount = executor.getActiveCount();
        int poolSize = executor.getPoolSize();
        int corePoolSize = executor.getCorePoolSize();
        int maximumPoolSize = executor.getMaximumPoolSize();
        int queueSize = executor.getQueue().size();
        int remainSize = executor.getQueue().remainingCapacity();
        String content = "tomcat线程池：{}，ActiveCount：{}， PoolSize：{}，CorePoolSize：{}，"
                + "MaximumPoolSize：{}，队列总大小：{}，已使用大小：{}，剩余：{}，已完成任务数：{}";
        log.info(content, "tomcat-threads", activeCount, poolSize, corePoolSize, maximumPoolSize
                , queueSize + remainSize, queueSize, remainSize, taskCount);
        doCollect(tpMonitorConfig.getApplicationName(), "tomcat-threads-" + port, taskCount
                , activeCount, poolSize, corePoolSize, maximumPoolSize, queueSize, remainSize);
    }
}
