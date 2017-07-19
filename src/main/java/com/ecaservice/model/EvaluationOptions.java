package com.ecaservice.model;

import lombok.Data;

/**
 * Implements evaluation method options.
 * @author Roman Batygin
 */
@Data
public class EvaluationOptions {

    private EvaluationMethod evaluationMethod;

    private Integer numFolds;

    private Integer numTests;

}
