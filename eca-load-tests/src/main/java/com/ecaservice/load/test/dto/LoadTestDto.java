package com.ecaservice.load.test.dto;

import com.ecaservice.load.test.entity.ExecutionStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import eca.core.evaluation.EvaluationMethod;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

import static com.ecaservice.load.test.dto.FieldConstraints.DATE_TIME_PATTERN;

/**
 * Load test dto model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Load test model")
public class LoadTestDto {

    /**
     * Test uuid
     */
    @ApiModelProperty(value = "Test uuid", required = true)
    private String testUuid;

    /**
     * Test creation date
     */
    @ApiModelProperty(value = "Test creation date")
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime created;

    /**
     * Started date
     */
    @ApiModelProperty(value = "Started date")
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime started;

    /**
     * Finished date
     */
    @ApiModelProperty(value = "Finished date")
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime finished;

    /**
     * Requests number to eca - server
     */
    @ApiModelProperty(value = "Requests number to eca - server")
    private Integer numRequests;

    /**
     * Threads number for requests sending
     */
    @ApiModelProperty(value = "Threads number for requests sending")
    private Integer numThreads;

    /**
     * Evaluation method
     */
    @ApiModelProperty(value = "Evaluation method")
    private EvaluationMethod evaluationMethod;

    /**
     * Folds number for k * V cross - validation method
     */
    @ApiModelProperty(value = "Folds number for k * V cross - validation method")
    private Integer numFolds;

    /**
     * Tests number for k * V cross - validation method
     */
    @ApiModelProperty(value = "Tests number for k * V cross - validation method")
    private Integer numTests;

    /**
     * Seed value for random generator
     */
    @ApiModelProperty(value = "Seed value for random generator")
    private Integer seed;

    /**
     * Test execution status
     */
    @ApiModelProperty(value = "Test execution status")
    private ExecutionStatus executionStatus;

    /**
     * Details string
     */
    @ApiModelProperty(value = "Details string")
    private String details;
}
