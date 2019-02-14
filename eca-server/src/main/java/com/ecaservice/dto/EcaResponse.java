package com.ecaservice.dto;

import com.ecaservice.model.TechnicalStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Response basic model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "ECA response model")
public class EcaResponse {

    /**
     * Request unique identifier
     */
    @ApiModelProperty(value = "Request unique identifier", required = true)
    private String requestId;

    /**
     * Technical status
     */
    @ApiModelProperty(value = "Technical status", required = true)
    private TechnicalStatus status;

    /**
     * Error message
     */
    @ApiModelProperty(value = "Error message")
    private String errorMessage;
}
