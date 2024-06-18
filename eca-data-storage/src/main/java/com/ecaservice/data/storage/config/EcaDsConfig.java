package com.ecaservice.data.storage.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

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

    /**
     * Supported training data file extensions
     */
    private List<String> supportedDataFileExtensions = newArrayList();
}
