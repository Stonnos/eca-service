package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Create experiment result dto model.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Create experiment result model")
public class CreateExperimentResultDto {

    /**
     * Experiment request id
     */
    @ApiModelProperty(value = "Experiment request id", required = true)
    private String requestId;

    /**
     * Is experiment created?
     */
    @ApiModelProperty(value = "Experiment creation boolean flag", required = true)
    private Boolean created;

    /**
     * Error message
     */
    @ApiModelProperty(value = "Error message")
    private String errorMessage;
}
