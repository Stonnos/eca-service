package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Classifier options response dto model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "ERS classifier options response model")
public class ClassifierOptionsResponseDto {

    /**
     * Classifier name
     */
    @ApiModelProperty(value = "Classifier name")
    private String classifierName;

    /**
     * Classifier options config
     */
    @ApiModelProperty(value = "Classifier input options json config")
    private String options;
}
