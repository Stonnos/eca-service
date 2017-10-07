package com.ecaservice.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

/**
 * Cross - validation configuration class.
 *
 * @author Roman Batygin
 */
@Data
public class CrossValidationConfig {

    /**
     * Number of folds in k * V cross validation method
     */
    @Value("${cross-validation.numFolds:10}")
    private Integer numFolds;

    /**
     * Number of tests in k * V cross validation method
     */
    @Value("${cross-validation.numTests:10}")
    private Integer numTests;

    /**
     * Seed for random generator
     */
    @Value("${cross-validation.seed:3}")
    private Integer seed;

    /**
     * Timeout value in minutes
     */
    @Value("${cross-validation.timeout:60}")
    private Integer timeout;
}
