package com.ecaservice.external.api.test.dto;

import com.ecaservice.test.common.model.MatchResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * Evaluation request auto test dto model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Evaluation request auto test dto model")
public class EvaluationRequestAutoTestDto extends AbstractAutoTestDto {

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
}
