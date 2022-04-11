package com.ecaservice.external.api.test.dto;

import com.ecaservice.external.api.dto.ResponseCode;
import com.ecaservice.test.common.model.MatchResult;
import com.ecaservice.test.common.model.TestResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * Auto test dto model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Auto test dto model")
public class AutoTestDto extends BaseTestDto {

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
     * Test result
     */
    @Schema(description = "Test result")
    private TestResult testResult;

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
