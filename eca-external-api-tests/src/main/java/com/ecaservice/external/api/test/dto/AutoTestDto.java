package com.ecaservice.external.api.test.dto;

import com.ecaservice.external.api.dto.ResponseCode;
import com.ecaservice.test.common.model.ExecutionStatus;
import com.ecaservice.test.common.model.MatchResult;
import com.ecaservice.test.common.model.TestResult;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.ecaservice.external.api.test.dto.FieldConstraints.DATE_TIME_PATTERN;

/**
 * Auto test dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Auto test dto model")
public class AutoTestDto {

    /**
     * Request id
     */
    @Schema(description = "Request id")
    private String requestId;

    /**
     * Display name (Test description)
     */
    @Schema(description = "Display name (Test description)")
    private String displayName;

    /**
     * Test execution status
     */
    @Schema(description = "Test execution status")
    private ExecutionStatus executionStatus;

    /**
     * Test result
     */
    @Schema(description = "Test result")
    private TestResult testResult;

    /**
     * Total time
     */
    @Schema(description = "Total time", pattern = "HH:mm:ss:SSSS", example = "00:00:35:5467")
    private String totalTime;

    /**
     * Total matched
     */
    @Schema(description = "Total matched")
    private int totalMatched;

    /**
     * Total not matched
     */
    @Schema(description = "Total not matched")
    private int totalNotMatched;

    /**
     * Total not found
     */
    @Schema(description = "Total not found")
    private int totalNotFound;

    /**
     * Created date
     */
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Schema(description = "Created date")
    private LocalDateTime created;

    /**
     * Started date
     */
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Schema(description = "Started date")
    private LocalDateTime started;

    /**
     * Finished date
     */
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Schema(description = "Finished date")
    private LocalDateTime finished;

    /**
     * Expected response code
     */
    @Schema(description = "Expected response code")
    private ResponseCode expectedResponseCode;

    /**
     * Actual response code
     */
    @Schema(description = "Actual response code")
    private ResponseCode actualResponseCode;

    /**
     * Response code match result
     */
    @Schema(description = "Response code match result")
    private MatchResult responseCodeMatchResult;

    /**
     * Expected model url
     */
    @Schema(description = "Expected model url")
    private String expectedModelUrl;

    /**
     * Actual model url
     */
    @Schema(description = "Actual model url")
    private String actualModelUrl;

    /**
     * Model url match result
     */
    @Schema(description = "Model url match result")
    private MatchResult modelUrlMatchResult;

    /**
     * Expected pct correct
     */
    @Schema(description = "Expected pct correct")
    private BigDecimal expectedPctCorrect;

    /**
     * Actual pct correct
     */
    @Schema(description = "Actual pct correct")
    private BigDecimal actualPctCorrect;

    /**
     * Pct correct match result
     */
    @Schema(description = "Pct correct match result")
    private MatchResult pctCorrectMatchResult;

    /**
     * Expected pct incorrect
     */
    @Schema(description = "Expected pct incorrect")
    private BigDecimal expectedPctIncorrect;

    /**
     * Actual pct incorrect
     */
    @Schema(description = "Actual pct incorrect")
    private BigDecimal actualPctIncorrect;

    /**
     * Pct incorrect match result
     */
    @Schema(description = "Pct incorrect match result")
    private MatchResult pctIncorrectMatchResult;

    /**
     * Expected mean absolute error
     */
    @Schema(description = "Expected mean absolute error")
    private BigDecimal expectedMeanAbsoluteError;

    /**
     * Actual mean absolute error
     */
    @Schema(description = "Actual mean absolute error")
    private BigDecimal actualMeanAbsoluteError;

    /**
     * Mean absolute error match result
     */
    @Schema(description = "Mean absolute error match result")
    private MatchResult meanAbsoluteErrorMatchResult;

    /**
     * Request json
     */
    @Schema(description = "Request json")
    private String request;

    /**
     * Response json
     */
    @Schema(description = "Response json")
    private String response;
}
