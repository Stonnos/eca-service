package com.ecaservice.model;

import com.ecaservice.model.entity.EvaluationMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Evaluation request model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationRequest {

    /**
     * Remote ip address
     */
    private String ipAddress;

    /**
     * Request date
     */
    private LocalDateTime requestDate;

    /**
     * Input data
     */
    private InputData inputData;

    /**
     * Evaluation method
     */
    private EvaluationMethod evaluationMethod;

    /**
     * Number of folds in k * V cross validation method
     */
    private Integer numFolds;

    /**
     * Number of tests in k * V cross validation method
     */
    private Integer numTests;
}
