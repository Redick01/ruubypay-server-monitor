package io.redick.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Redick01
 */
@ConfigurationProperties(prefix = "tp.monitor")
@Data
public class TpMonitorConfig {

    private String applicationName;

    private Integer monitorInterval = 5;
}
