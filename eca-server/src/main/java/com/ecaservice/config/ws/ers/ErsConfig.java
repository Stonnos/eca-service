package com.ecaservice.config.ws.ers;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

/**
 * Evaluation results service config.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("ers")
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
     * Use optimal classifier options cache?
     */
    private Boolean useClassifierOptionsCache;

    /**
     * Classifier options cache duration in days
     */
    private Integer classifierOptionsCacheDurationInDays;

    /**
     * Ssl config
     */
    private SslConfig ssl;

    /**
     * Ssl config.
     */
    @Data
    public static class SslConfig {

        /**
         * Trust self signed certificate?
         */
        private Boolean trustSelfSigned;

        /**
         * Trust store resource
         */
        private Resource trustStore;

        /**
         * Trust store password
         */
        private String trustStorePassword;
    }
}
