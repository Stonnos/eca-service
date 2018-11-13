package com.ecaservice.web.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Filter request model.
 *
 * @author Roman Batygin
 */
@Data
public class FilterRequestDto {

    /**
     * Column name
     */
    @ApiModelProperty(notes = "Field name")
    private String name;

    /**
     * Column value
     */
    @ApiModelProperty(notes = "Field value")
    private String value;

    /**
     * Filter type {@link FilterType}
     */
    @ApiModelProperty(notes = "Filter type")
    private FilterType filterType;

    /**
     * Match mode type {@link MatchMode}
     */
    @ApiModelProperty(notes = "Filter match mode")
    private MatchMode matchMode;
}
