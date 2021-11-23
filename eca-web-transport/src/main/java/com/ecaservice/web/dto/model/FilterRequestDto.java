package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUES_LIST_MAX_LENGTH;

/**
 * Filter request model.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Filter request model")
public class FilterRequestDto {

    /**
     * Column name
     */
    @NotBlank
    @Size(max = MAX_LENGTH_255)
    @Schema(description = "Filter column name", example = "field")
    private String name;

    /**
     * Column values to filter
     */
    @ArraySchema(schema = @Schema(description = "Column values to filter", maxLength = MAX_LENGTH_255))
    @Size(max = VALUES_LIST_MAX_LENGTH)
    private List<@Size(max = MAX_LENGTH_255) String> values;

    /**
     * Match mode type {@link MatchMode}
     */
    @NotNull
    @Size(max = MAX_LENGTH_255)
    @Schema(description = "Match mode type", example = "LIKE")
    private MatchMode matchMode;
}
