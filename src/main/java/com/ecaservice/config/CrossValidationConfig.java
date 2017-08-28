package com.ecaservice.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Cross - validation configuration class.
 *
 * @author Roman Batygin
 */
@Data
@Component
public class CrossValidationConfig {

    @Value("${cross-validation.numFolds:10}")
    private Integer numFolds;

    @Value("${cross-validation.numTests:10}")
    private Integer numTests;

    @Value("${cross-validation.seed:3}")
    private Integer seed;

    @Value("${cross-validation.timeout:10}")
    private Integer timeout;
}
