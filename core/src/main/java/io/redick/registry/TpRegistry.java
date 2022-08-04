package io.redick.registry;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Redick01
 */
public final class TpRegistry {

    public static final Map<String, ThreadPoolExecutor> TP_REGISTRY = Maps.newConcurrentMap();

    /**
     * refresh thread pool registry.
     * @param name thread pool name
     * @param executor {@link ThreadPoolExecutor}
     */
    public static void refresh(String name, ThreadPoolExecutor executor) {
        TP_REGISTRY.putIfAbsent(name, executor);
    }
}
