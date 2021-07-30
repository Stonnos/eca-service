package com.ecaservice.load.test.dto;

import com.ecaservice.test.common.model.ExecutionStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import eca.core.evaluation.EvaluationMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

import static com.ecaservice.load.test.dto.FieldConstraints.DATE_TIME_PATTERN;

/**
 * Load test dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Load test model")
public class LoadTestDto {

    /**
     * Test uuid
     */
    @Schema(description = "Test uuid", required = true)
    private String testUuid;

    /**
     * Test creation date
     */
    @Schema(description = "Test creation date")
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime created;

    /**
     * Started date
     */
    @Schema(description = "Started date")
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime started;

    /**
     * Finished date
     */
    @Schema(description = "Finished date")
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime finished;

    /**
     * Requests number to eca - server
     */
    @Schema(description = "Requests number to eca - server")
    private Integer numRequests;

    /**
     * Threads number for requests sending
     */
    @Schema(description = "Threads number for requests sending")
    private Integer numThreads;

    /**
     * Evaluation method
     */
    @Schema(description = "Evaluation method")
    private EvaluationMethod evaluationMethod;

    /**
     * Folds number for k * V cross - validation method
     */
    @Schema(description = "Folds number for k * V cross - validation method")
    private Integer numFolds;

    /**
     * Tests number for k * V cross - validation method
     */
    @Schema(description = "Tests number for k * V cross - validation method")
    private Integer numTests;

    /**
     * Seed value for random generator
     */
    @Schema(description = "Seed value for random generator")
    private Integer seed;

    /**
     * Test execution status
     */
    @Schema(description = "Test execution status")
    private ExecutionStatus executionStatus;

    /**
     * Details string
     */
    @Schema(description = "Details string")
    private String details;
}
