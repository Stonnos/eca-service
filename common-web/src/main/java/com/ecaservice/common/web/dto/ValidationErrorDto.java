package com.ecaservice.common.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Validation error dto.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Validation error model")
public class ValidationErrorDto implements Serializable {

    /**
     * Field name
     */
    @ApiModelProperty(value = "Field name")
    private String fieldName;

    /**
     * Error code
     */
    @ApiModelProperty(value = "Error code")
    private String code;

    /**
     * Error message
     */
    @ApiModelProperty(value = "Error message")
    private String errorMessage;
}
