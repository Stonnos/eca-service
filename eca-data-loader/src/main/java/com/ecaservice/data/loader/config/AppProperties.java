package com.ecaservice.data.loader.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Application properties.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("app")
public class AppProperties {

    /**
     * Date format for date attributes
     */
    private String dateFormat;
}
