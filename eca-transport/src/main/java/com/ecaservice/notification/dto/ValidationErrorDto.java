package com.ecaservice.notification.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Validation error dto.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Validation error model")
public class ValidationErrorDto {

    /**
     * Field name
     */
    @ApiModelProperty(value = "Field name")
    private String fieldName;

    /**
     * Error message
     */
    @ApiModelProperty(value = "Error message")
    private String errorMessage;
}
