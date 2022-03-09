package com.ecaservice.auto.test.model.evaluation;

import com.ecaservice.test.common.model.MatchResult;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Evaluation results details match.
 *
 * @author Roman Batygin
 */
@Data
public class EvaluationResultsDetailsMatch {

    /**
     * Ers request id
     */
    private String requestId;

    /**
     * Creation date
     */
    private LocalDateTime created;

    /**
     * Expected test instances number
     */
    private Integer numTestInstances;

    /**
     * Actual test instances number
     */
    private Integer actualTestInstances;

    /**
     * Test instances number match result
     */
    private MatchResult testInstancesMatchResult;

    /**
     * Expected correctly classified instances number
     */
    private Integer expectedNumCorrect;

    /**
     * Actual correctly classified instances number
     */
    private Integer actualNumCorrect;

    /**
     * Correctly classified instances number match result
     */
    private MatchResult numCorrectMatchResult;

    /**
     * Expected incorrectly classified instances number
     */
    private Integer expectedNumIncorrect;

    /**
     * Actual incorrectly classified instances number
     */
    private Integer actualNumIncorrect;

    /**
     * Incorrectly classified instances number match result
     */
    private MatchResult numIncorrectMatchResult;

    /**
     * Expected correctly classified instances percent
     */
    private BigDecimal expectedPctCorrect;

    /**
     * Actual correctly classified instances percent
     */
    private BigDecimal actualPctCorrect;

    /**
     * Correctly classified instances percent match result
     */
    private MatchResult pctCorrectMatchResult;

    /**
     * Expected incorrectly classified instances percent
     */
    private BigDecimal expectedPctIncorrect;

    /**
     * Actual incorrectly classified instances percent
     */
    private BigDecimal actualPctIncorrect;

    /**
     * Incorrectly classified instances percent match result
     */
    private MatchResult pctIncorrectMatchResult;

    /**
     * Expected classification mean absolute error
     */
    private BigDecimal expectedMeanAbsoluteError;

    /**
     * Actual classification mean absolute error
     */
    private BigDecimal actualMeanAbsoluteError;

    /**
     * Classification mean absolute error match result
     */
    private MatchResult meanAbsoluteErrorMatchResult;

    /**
     * Classification root mean squared error
     */
    private BigDecimal expectedRootMeanSquaredError;

    /**
     * Actual classification root mean squared error
     */
    private BigDecimal actualRootMeanSquaredError;

    /**
     * Classification root mean squared error match result
     */
    private MatchResult rootMeanSquaredErrorMatchResult;

    /**
     * Expected maximum AUC value
     */
    private BigDecimal expectedMaxAucValue;

    /**
     * Actual maximum AUC value
     */
    private BigDecimal actualMaxAucValue;

    /**
     * Maximum AUC value match result
     */
    private MatchResult maxAucValueMatchResult;

    /**
     * Expected classifier variance error
     */
    private BigDecimal expectedVarianceError;

    /**
     * Actual classifier variance error
     */
    private BigDecimal actualVarianceError;

    /**
     * Classifier variance error match result
     */
    private MatchResult varianceErrorMatchResult;

    /**
     * Expected classifier error Student confidence interval lower bound
     */
    private BigDecimal expectedConfidenceIntervalLowerBound;

    /**
     * Actual classifier error Student confidence interval lower bound
     */
    private BigDecimal actualConfidenceIntervalLowerBound;

    /**
     * Classifier error Student confidence interval lower bound match result
     */
    private MatchResult confidenceIntervalLowerBoundMatchResult;

    /**
     * Expected classifier error Student confidence interval upper bound
     */
    private BigDecimal expectedConfidenceIntervalUpperBound;

    /**
     * Actual classifier error Student confidence interval upper bound
     */
    private BigDecimal actualConfidenceIntervalUpperBound;

    /**
     * Classifier error Student confidence interval upper bound match result
     */
    private MatchResult confidenceIntervalUpperBoundMatchResult;

    /**
     * Classification costs reports list
     */
    private List<ClassificationCostsDetailsMatch> classificationCostsDetails;

    /**
     * Confusion matrix
     */
    private List<ConfusionMatrixDetailsMatch> confusionMatrixDetails;
}
