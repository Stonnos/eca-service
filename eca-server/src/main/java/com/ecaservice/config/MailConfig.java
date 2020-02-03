package com.ecaservice.config;

import com.ecaservice.model.entity.RequestStatus;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import java.util.Map;

/**
 * Experiment email configuration class.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("experiment.mail")
public class MailConfig {

    /**
     * Message templates map
     */
    private Map<RequestStatus, String> messageTemplatesMap;

    /**
     * Source email
     */
    private String from;

    /**
     * Subject string
     */
    private String subject;

    /**
     * Email web service url
     */
    private String serviceUrl;

    /**
     * Is service enabled?
     */
    private Boolean enabled;

    /**
     * Ssl config
     */
    private SslConfig ssl;

    /**
     * Ssl config.
     */
    @Data
    @ConfigurationProperties("experiment.mail.ssl")
    public static class SslConfig {

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
