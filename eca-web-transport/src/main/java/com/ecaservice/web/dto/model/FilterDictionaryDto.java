package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;

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
    @Schema(description = "Filter dictionary name", example = "Dictionary name", maxLength = MAX_LENGTH_255)
    private String name;

    /**
     * Values list for reference filter type
     */
    @Schema(description = "Filter dictionary values")
    private List<FilterDictionaryValueDto> values;
}
