package com.ecaservice.load.test.dto;

import com.ecaservice.load.test.entity.TestExecutionMode;
import eca.core.evaluation.EvaluationMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import static com.ecaservice.load.test.dto.FieldConstraints.MAX_DURATION;
import static com.ecaservice.load.test.dto.FieldConstraints.MAX_NUM_FOLDS;
import static com.ecaservice.load.test.dto.FieldConstraints.MAX_NUM_REQUESTS;
import static com.ecaservice.load.test.dto.FieldConstraints.MAX_NUM_TESTS;
import static com.ecaservice.load.test.dto.FieldConstraints.MAX_NUM_THREADS;
import static com.ecaservice.load.test.dto.FieldConstraints.MIN_DURATION;
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
@Schema(description = "Load test request dto model")
public class LoadTestRequest {

    /**
     * Test execution mode
     */
    @NotNull
    @Schema(description = "Test execution mode", example = "REQUESTS_LIMIT")
    private TestExecutionMode executionMode;

    /**
     * Requests number to eca - server
     */
    @Min(MIN_NUM_REQUESTS)
    @Max(MAX_NUM_REQUESTS)
    @Schema(description = "Requests number to eca - server", example = "250")
    private Integer numRequests;

    /**
     * Test duration in seconds
     */
    @Min(MIN_DURATION)
    @Max(MAX_DURATION)
    @Schema(description = "Test duration in seconds", example = "30")
    private Long durationSeconds;

    /**
     * Threads number for requests sending
     */
    @Min(MIN_NUM_THREADS)
    @Max(MAX_NUM_THREADS)
    @Schema(description = "Threads number for requests sending", example = "10")
    private Integer numThreads;

    /**
     * Evaluation method
     */
    @NotNull
    @Schema(description = "Evaluation method", example = "TRAINING_DATA")
    private EvaluationMethod evaluationMethod;

    /**
     * Folds number for k * V cross - validation method
     */
    @Min(MIN_NUM_FOLDS)
    @Max(MAX_NUM_FOLDS)
    @Schema(description = "Folds number for k * V cross - validation method", example = "10")
    private Integer numFolds;

    /**
     * Tests number for k * V cross - validation method
     */
    @Min(MIN_NUM_TESTS)
    @Max(MAX_NUM_TESTS)
    @Schema(description = "Tests number for k * V cross - validation method", example = "1")
    private Integer numTests;

    /**
     * Seed value for random generator
     */
    @Schema(description = "Seed value for random generator")
    private Integer seed;
}
