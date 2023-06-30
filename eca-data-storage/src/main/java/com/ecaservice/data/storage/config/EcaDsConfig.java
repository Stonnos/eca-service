package com.ecaservice.data.storage.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Data storage config.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("eca-ds")
public class EcaDsConfig {

    /**
     * Date format for date attributes
     */
    private String dateFormat;

    /**
     * Batch size for data saving
     */
    private Integer batchSize;

    /**
     * Reports config path
     */
    private String reportsPath;
}
