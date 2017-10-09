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

    @Value("${experiment.resultSize:100}")
    private Integer resultSize;

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

    @Value("${experiment.maximumFractionDigits:4}")
    private Integer maximumFractionDigits;

    @Value("${experiment.ensemble.numIterations:25}")
    private Integer ensembleNumIterations;

    @Value("${experiment.timeout:5}")
    private Integer timeout;

    @Value("${experiment.delay:60000}")
    private String delay;

}
