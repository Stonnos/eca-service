package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Filter field dto model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Filter field model")
public class FilterFieldDto {

    /**
     * Field name
     */
    @ApiModelProperty(value = "Field name")
    private String fieldName;

    /**
     * Field description
     */
    @ApiModelProperty(value = "Field description")
    private String description;

    /**
     * Field order
     */
    @ApiModelProperty(value = "Field order")
    private int fieldOrder;

    /**
     * Filter type
     */
    @ApiModelProperty(value = "Filter field type")
    private FilterFieldType filterFieldType;

    /**
     * Filter match mode
     */
    @ApiModelProperty(value = "Filter match mode")
    private MatchMode matchMode;

    /**
     * Filter dictionary
     */
    @ApiModelProperty(value = "Filter dictionary")
    private FilterDictionaryDto dictionary;
}
