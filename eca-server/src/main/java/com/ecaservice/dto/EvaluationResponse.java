package com.ecaservice.dto;

import com.ecaservice.dto.json.EvaluationResultsSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eca.core.evaluation.EvaluationResults;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Evaluation response model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "ECA evaluation response model")
public class EvaluationResponse extends EcaResponse {

    /**
     * Evaluation results
     */
    @ApiModelProperty(value = "Evaluation results")
    @JsonSerialize(using = EvaluationResultsSerializer.class)
    private EvaluationResults evaluationResults;

}
