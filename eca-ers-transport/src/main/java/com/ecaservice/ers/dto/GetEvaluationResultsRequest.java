package com.ecaservice.ers.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.ecaservice.ers.dto.Constraints.UUID_MAX_SIZE;
import static com.ecaservice.ers.dto.Constraints.UUID_PATTERN;

/**
 * Get evaluation results request model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Get evaluation results request model")
public class GetEvaluationResultsRequest {

    /**
     * Request id
     */
    @NotBlank
    @Pattern(regexp = UUID_PATTERN)
    @Size(max = UUID_MAX_SIZE)
    @ApiModelProperty(value = "Request id", example = "1d2de514-3a87-4620-9b97-c260e24340de", required = true)
    private String requestId;
}
