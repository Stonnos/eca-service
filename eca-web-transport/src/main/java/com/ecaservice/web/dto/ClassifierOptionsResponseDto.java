package com.ecaservice.web.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Classifier options response dto model.
 *
 * @author Roman Batygin
 */
@Data
public class ClassifierOptionsResponseDto {

    /**
     * Classifier name
     */
    @ApiModelProperty(notes = "Classifier name")
    private String classifierName;

    /**
     * Classifier options config
     */
    @ApiModelProperty(notes = "Classifier input options json config")
    private String options;
}
