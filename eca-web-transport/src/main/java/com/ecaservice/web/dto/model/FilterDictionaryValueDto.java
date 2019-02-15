package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Filter field dictionary value dto model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Filter dictionary value model")
public class FilterDictionaryValueDto {

    /**
     * Label string
     */
    @ApiModelProperty(value = "Filter dictionary field label")
    private String label;

    /**
     * String value
     */
    @ApiModelProperty(value = "Filter dictionary field value")
    private String value;
}
