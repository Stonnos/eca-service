package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Filter dictionary dto model.
 *
 * @author Roman Batygin
 */
@Data
public class FilterDictionaryDto {

    /**
     * Dictionary name
     */
    @ApiModelProperty(name = "Filter dictionary name")
    private String name;

    /**
     * Values list for reference filter type
     */
    @ApiModelProperty(name = "Filter dictionary values")
    private List<FilterDictionaryValueDto> values;
}
