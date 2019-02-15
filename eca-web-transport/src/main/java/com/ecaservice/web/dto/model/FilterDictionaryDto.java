package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Filter dictionary dto model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Filter dictionary model")
public class FilterDictionaryDto {

    /**
     * Dictionary name
     */
    @ApiModelProperty(value = "Filter dictionary name")
    private String name;

    /**
     * Values list for reference filter type
     */
    @ApiModelProperty(value = "Filter dictionary values")
    private List<FilterDictionaryValueDto> values;
}
