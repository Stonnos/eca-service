package com.ecaservice.external.api.test.entity;

import com.ecaservice.test.common.model.MatchResult;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.math.BigDecimal;

import static com.ecaservice.external.api.test.util.Constraints.PRECISION;
import static com.ecaservice.external.api.test.util.Constraints.SCALE;

/**
 * Evaluation request auto test persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "evaluation_request_auto_test")
public class EvaluationRequestAutoTestEntity extends AutoTestEntity {

    /**
     * Expected pct correct
     */
    @Column(name = "expected_pct_correct", precision = PRECISION, scale = SCALE)
    private BigDecimal expectedPctCorrect;

    /**
     * Actual pct correct
     */
    @Column(name = "actual_pct_correct", precision = PRECISION, scale = SCALE)
    private BigDecimal actualPctCorrect;

    /**
     * Pct correct match result
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "pct_correct_match_result")
    private MatchResult pctCorrectMatchResult;

    /**
     * Expected pct incorrect
     */
    @Column(name = "expected_pct_incorrect", precision = PRECISION, scale = SCALE)
    private BigDecimal expectedPctIncorrect;

    /**
     * Actual pct incorrect
     */
    @Column(name = "actual_pct_incorrect", precision = PRECISION, scale = SCALE)
    private BigDecimal actualPctIncorrect;

    /**
     * Pct incorrect match result
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "pct_incorrect_match_result")
    private MatchResult pctIncorrectMatchResult;

    /**
     * Expected mean absolute error
     */
    @Column(name = "expected_mean_absolute_error", precision = PRECISION, scale = SCALE)
    private BigDecimal expectedMeanAbsoluteError;

    /**
     * Actual mean absolute error
     */
    @Column(name = "actual_mean_absolute_error", precision = PRECISION, scale = SCALE)
    private BigDecimal actualMeanAbsoluteError;

    /**
     * Mean absolute error match result
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "mean_absolute_error_match_result")
    private MatchResult meanAbsoluteErrorMatchResult;
}
