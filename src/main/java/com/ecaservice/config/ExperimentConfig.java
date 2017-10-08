package com.ecaservice.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

/**
 * Experiment configuration class.
 *
 * @author Roman Batygin
 */
@Data
public class ExperimentConfig {

    @Value("${experiment.numIterations:10000}")
    private Integer numIterations;

    @Value("${experiment.dataFileFormat}")
    private String dataFileFormat;

    @Value("${experiment.fileFormat}")
    private String fileFormat;

    @Value("${experiment.dataStoragePath}")
    private String dataStoragePath;

    @Value("${experiment.storagePath}")
    private String storagePath;

    @Value("${experiment.dateFormat}")
    private String dateFormat;

    @Value("${experiment.timeout:5}")
    private Integer timeout;

}
