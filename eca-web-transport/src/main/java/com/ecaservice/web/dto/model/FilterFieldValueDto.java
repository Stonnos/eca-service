package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Filter field value dto model.
 *
 * @author Roman Batygin
 */
@Data
public class FilterFieldValueDto {

    /**
     * Label string
     */
    @ApiModelProperty(notes = "Filter field label")
    private String label;

    /**
     * String value
     */
    @ApiModelProperty(notes = "Filter field value")
    private String value;
}
