package com.ecaservice.core.filter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Core filter properties.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("filter")
public class CoreFilterProperties {

    private static final String FILTER_TEMPLATES_DICTIONARIES_JSON =
            "classpath*:filter-templates/dictionaries/**/*.json";
    private static final String FILTER_TEMPLATES_JSON = "classpath*:filter-templates/templates/**/*.json";

    /**
     * Templates location
     */
    private String templatesLocation = FILTER_TEMPLATES_JSON;

    /**
     * Dictionaries location
     */
    private String dictionariesLocation = FILTER_TEMPLATES_DICTIONARIES_JSON;
}
