package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * Filter dictionary dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Filter dictionary model")
public class FilterDictionaryDto {

    /**
     * Dictionary name
     */
    @Schema(description = "Filter dictionary name")
    private String name;

    /**
     * Values list for reference filter type
     */
    @Schema(description = "Filter dictionary values")
    private List<FilterDictionaryValueDto> values;
}
