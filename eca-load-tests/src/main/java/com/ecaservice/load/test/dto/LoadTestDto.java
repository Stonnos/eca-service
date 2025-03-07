package com.ecaservice.load.test.dto;

import com.ecaservice.test.common.model.ExecutionStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import eca.core.evaluation.EvaluationMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
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
    @Schema(description = "Test uuid", requiredMode = Schema.RequiredMode.REQUIRED)
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
     * Tests duration in seconds
     */
    @Schema(description = "Tests duration in seconds")
    private Long durationSeconds;

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
     * Total tests count
     */
    @Schema(description = "Total tests count")
    private Integer totalCount;

    /**
     * Passed tests count
     */
    @Schema(description = "Passed tests count")
    private Integer passedCount;

    /**
     * Failed tests count
     */
    @Schema(description = "Failed tests count")
    private Integer failedCount;

    /**
     * Error tests count
     */
    @Schema(description = "Error tests count")
    private Integer errorCount;

    /**
     * Request total time
     */
    @Schema(description = "Request total time")
    private String totalTime;

    /**
     * Tps value
     */
    @Schema(description = "Tps value")
    private BigDecimal tps;

    /**
     * Details string
     */
    @Schema(description = "Details string")
    private String details;
}
