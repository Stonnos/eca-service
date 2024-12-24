package com.ecaservice.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Classifiers properties.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("classifiers")
public class ClassifiersProperties {

    /**
     * Evaluation timeout value in minutes
     */
    private Integer evaluationTimeoutMinutes;

    /**
     * Delay value for scheduler in seconds
     */
    private Integer delaySeconds;

    /**
     * Evaluation lock ttl in seconds
     */
    private Integer lockTtlSeconds;

    /**
     * Batch size for processing
     */
    private Integer batchSize;

    /**
     * Thread pool size
     */
    private Integer threadPoolSize;
}
