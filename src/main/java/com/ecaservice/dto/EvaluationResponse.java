package com.ecaservice.dto;

import com.ecaservice.dto.json.EvaluationResponseSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eca.core.evaluation.EvaluationResults;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Evaluation response model.
 *
 * @author Roman Batygin
 */
@Data
@JsonSerialize(using = EvaluationResponseSerializer.class)
public class EvaluationResponse extends EcaResponse {

    /**
     * Evaluation results
     */
    @ApiModelProperty(notes = "Evaluation results")
    private EvaluationResults evaluationResults;

}
