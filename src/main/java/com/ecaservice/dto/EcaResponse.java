package com.ecaservice.dto;

import com.ecaservice.model.TechnicalStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Response basic model.
 *
 * @author Roman Batygin
 */
@Data
public class EcaResponse {

    /**
     * Technical status
     */
    @ApiModelProperty(notes = "Technical status", required = true)
    private TechnicalStatus status;

    /**
     * Error message
     */
    @ApiModelProperty(notes = "Error message")
    private String errorMessage;
}
