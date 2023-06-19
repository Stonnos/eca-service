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
     * Timeout value in minutes
     */
    private Integer timeout;
}
