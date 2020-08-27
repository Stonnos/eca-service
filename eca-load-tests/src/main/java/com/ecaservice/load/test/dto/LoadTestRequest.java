package com.ecaservice.load.test.dto;

import eca.core.evaluation.EvaluationMethod;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import static com.ecaservice.load.test.dto.FieldConstraints.MAX_NUM_FOLDS;
import static com.ecaservice.load.test.dto.FieldConstraints.MAX_NUM_REQUESTS;
import static com.ecaservice.load.test.dto.FieldConstraints.MAX_NUM_TESTS;
import static com.ecaservice.load.test.dto.FieldConstraints.MAX_NUM_THREADS;
import static com.ecaservice.load.test.dto.FieldConstraints.MIN_NUM_FOLDS;
import static com.ecaservice.load.test.dto.FieldConstraints.MIN_NUM_REQUESTS;
import static com.ecaservice.load.test.dto.FieldConstraints.MIN_NUM_TESTS;
import static com.ecaservice.load.test.dto.FieldConstraints.MIN_NUM_THREADS;

/**
 * Load test request dto model.
 *
 * @author Roman Batygin
 */
@Data
public class LoadTestRequest {

    /**
     * Requests number to eca - server
     */
    @Min(MIN_NUM_REQUESTS)
    @Max(MAX_NUM_REQUESTS)
    private Integer numRequests;

    /**
     * Threads number for requests sending
     */
    @Min(MIN_NUM_THREADS)
    @Max(MAX_NUM_THREADS)
    private Integer numThreads;

    /**
     * Evaluation method
     */
    @NotNull
    private EvaluationMethod evaluationMethod;

    /**
     * Folds number for k * V cross - validation method
     */
    @Min(MIN_NUM_FOLDS)
    @Max(MAX_NUM_FOLDS)
    private Integer numFolds;

    /**
     * Tests number for k * V cross - validation method
     */
    @Min(MIN_NUM_TESTS)
    @Max(MAX_NUM_TESTS)
    private Integer numTests;

    /**
     * Seed value for random generator
     */
    private Integer seed;
}
