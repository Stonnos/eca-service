package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Create classifier options dto model.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Create classifier options model")
public class CreateClassifierOptionsResultDto {

    /**
     * Classifier options id
     */
    @ApiModelProperty(value = "Classifier options id", required = true)
    private Long id;

    /**
     * Source file name
     */
    @ApiModelProperty(value = "Source file name", required = true)
    private String sourceFileName;

    /**
     * Is classifier options successfully saved?
     */
    @ApiModelProperty(value = "Classifier options saved boolean flag", required = true)
    private Boolean success;

    /**
     * Error message
     */
    @ApiModelProperty(value = "Error message")
    private String errorMessage;
}
