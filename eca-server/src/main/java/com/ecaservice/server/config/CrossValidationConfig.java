package com.ecaservice.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Cross - validation configuration class.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("cross-validation")
public class CrossValidationConfig {

    /**
     * Number of folds in k * V cross validation method
     */
    private Integer numFolds;

    /**
     * Number of tests in k * V cross validation method
     */
    private Integer numTests;

    /**
     * Seed for random generator
     */
    private Integer seed;

    /**
     * Timeout value in minutes
     */
    private Integer timeout;
}
