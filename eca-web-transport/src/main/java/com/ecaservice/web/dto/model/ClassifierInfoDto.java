package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Classifier info dto.
 *
 * @author Roman Batygin
 */
@Data
public class ClassifierInfoDto {

    /**
     * Classifier name
     */
    @ApiModelProperty(value = "Classifier name")
    private String classifierName;

    /**
     * Classifier input options map
     */
    @ApiModelProperty(value = "Classifier input options list")
    private List<InputOptionDto> inputOptions;
}
