package com.ecaservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Evaluation results service config.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("evaluationResultsServiceConfig")
public class ErsConfig {

    /**
     * Endpoint url
     */
    private String url;

    /**
     * Is results sending enabled?
     */
    private Boolean enabled;

    /**
     * Maximum threads number for thread pool executor
     */
    private Integer threadPoolSize;
}
