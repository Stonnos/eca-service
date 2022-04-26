package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;

/**
 * Field dictionary dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Field dictionary model")
public class FieldDictionaryDto {

    /**
     * Dictionary name
     */
    @Schema(description = "Field dictionary name", example = "Dictionary name", maxLength = MAX_LENGTH_255)
    private String name;

    /**
     * Values list for reference filter type
     */
    @ArraySchema(schema = @Schema(description = "Field dictionary values"))
    private List<FieldDictionaryValueDto> values;
}
