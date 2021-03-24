package com.ecaservice.ers.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Evaluation results response model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Evaluation results response model")
public class EvaluationResultsResponse {

    /**
     * Request id
     */
    @ApiModelProperty(value = "Request id")
    private String requestId;
    /**
     * Response status
     */
    @ApiModelProperty(value = "Response status")
    private ResponseStatus status;
}
