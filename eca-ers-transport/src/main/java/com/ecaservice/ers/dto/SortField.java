package com.ecaservice.ers.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static com.ecaservice.ers.dto.Constraints.MAX_LENGTH_255;
import static com.ecaservice.ers.dto.Constraints.MIN_1;

/**
 * Sort field model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Sort field model")
public class SortField {

    /**
     * Sort field name
     */
    @NotBlank
    @Size(min = MIN_1, max = MAX_LENGTH_255)
    @Schema(description = "Sort field name", example = "statistics.pctCorrect", required = true)
    private String fieldName;

    /**
     * Sort direction
     */
    @Schema(description = "Sort direction", maxLength = MAX_LENGTH_255)
    private SortDirection direction;
}
