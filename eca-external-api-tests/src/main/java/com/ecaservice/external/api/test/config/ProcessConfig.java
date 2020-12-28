package com.ecaservice.external.api.test.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * BPM process config.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("process")
public class ProcessConfig {

    /**
     * Process id
     */
    private String processId;
}
