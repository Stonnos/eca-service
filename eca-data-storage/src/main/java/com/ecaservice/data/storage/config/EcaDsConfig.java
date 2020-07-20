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
     * Maximum page size for paging requests
     */
    private Integer maxPageSize;

    /**
     * Date format for date attributes
     */
    private String dateFormat;

    /**
     * Batch size for data saving
     */
    private Integer batchSize;
}
