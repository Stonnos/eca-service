package com.ecaservice.server.config.ers;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Evaluation results service config.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("ers")
public class ErsConfig {

    /**
     * Use optimal classifier options cache?
     */
    private Boolean useClassifierOptionsCache;

    /**
     * Classifier options cache duration in days
     */
    private Integer classifierOptionsCacheDurationInDays;

    /**
     * Redelivery enabled?
     */
    private Boolean redelivery;

    /**
     * Page size (used for pagination)
     */
    private Integer pageSize;
}
