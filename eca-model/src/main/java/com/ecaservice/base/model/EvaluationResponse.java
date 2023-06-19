package com.ecaservice.base.model;

import com.ecaservice.base.model.databind.EvaluationResultsDeserializer;
import com.ecaservice.base.model.databind.EvaluationResultsSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eca.core.evaluation.EvaluationResults;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * Evaluation response model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EvaluationResponse extends EcaResponse {

    /**
     * Model download url
     */
    private String modelUrl;

    /**
     * Test instances number
     */
    private Integer numTestInstances;

    /**
     * Correctly classified instances number
     */
    private Integer numCorrect;

    /**
     * Incorrectly classified instances number
     */
    private Integer numIncorrect;

    /**
     * Correctly classified percentage
     */
    private BigDecimal pctCorrect;

    /**
     * Incorrectly classified percentage
     */
    private BigDecimal pctIncorrect;

    /**
     * Mean absolute error
     */
    private BigDecimal meanAbsoluteError;

    /**
     * Evaluation results
     */
    @JsonSerialize(using = EvaluationResultsSerializer.class)
    @JsonDeserialize(using = EvaluationResultsDeserializer.class)
    private EvaluationResults evaluationResults;
}
