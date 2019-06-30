package com.ecaservice.config.ers;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Evaluation results service config.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("ers-config")
public class ErsConfig {

    /**
     * Endpoint url
     */
    private String url;

    /**
     * Is results sending enabled?
     */
    private Boolean enabled;
}
