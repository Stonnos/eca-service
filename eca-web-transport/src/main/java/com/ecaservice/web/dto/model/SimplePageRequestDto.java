package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_PAGE_SIZE;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_0;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1;

/**
 * Simple page request model.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Simple page request model")
public class SimplePageRequestDto {

    /**
     * Page number
     */
    @NotNull
    @Min(VALUE_0)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "Page number", example = "0")
    private Integer page;

    /**
     * Page size
     */
    @NotNull
    @Min(VALUE_1)
    @Max(MAX_PAGE_SIZE)
    @Schema(description = "Page size", example = "25")
    private Integer size;
}
