package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Filter field dictionary value dto model.
 *
 * @author Roman Batygin
 */
@Data
public class FilterDictionaryValueDto {

    /**
     * Label string
     */
    @ApiModelProperty(notes = "Filter dictionary field label")
    private String label;

    /**
     * String value
     */
    @ApiModelProperty(notes = "Filter dictionary field value")
    private String value;
}
