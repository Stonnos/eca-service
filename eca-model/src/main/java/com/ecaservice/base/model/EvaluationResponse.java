package com.ecaservice.base.model;

import com.ecaservice.base.model.databind.EvaluationResultsDeserializer;
import com.ecaservice.base.model.databind.EvaluationResultsSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eca.core.evaluation.EvaluationResults;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Evaluation response model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EvaluationResponse extends EcaResponse {

    /**
     * Evaluation results
     */
    @JsonSerialize(using = EvaluationResultsSerializer.class)
    @JsonDeserialize(using = EvaluationResultsDeserializer.class)
    private EvaluationResults evaluationResults;

}
