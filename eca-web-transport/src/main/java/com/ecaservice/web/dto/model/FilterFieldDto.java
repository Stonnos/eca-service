package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Filter field dto model.
 *
 * @author Roman Batygin
 */
@Data
public class FilterFieldDto {

    /**
     * Field name
     */
    @ApiModelProperty(notes = "Field name")
    private String name;

    /**
     * Field description
     */
    @ApiModelProperty(notes = "Field description")
    private String description;

    /**
     * Field order
     */
    @ApiModelProperty(notes = "Field order")
    private int fieldOrder;

    /**
     * Filter type
     */
    @ApiModelProperty(notes = "Filter type")
    private FilterType filterType;

    /**
     * Filter match mode
     */
    @ApiModelProperty(notes = "Filter match mode")
    private MatchMode matchMode;

    /**
     * Filter filed values list
     */
    @ApiModelProperty(notes = "Filter filed values list")
    private List<FilterFieldValueDto> values;
}
