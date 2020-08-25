package com.ecaservice.classifier.options.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Classifiers options config.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("classifiers-options")
public class ClassifiersOptionsConfig {

    /**
     * Maximum fraction digits
     */
    private Integer maximumFractionDigits;
}
