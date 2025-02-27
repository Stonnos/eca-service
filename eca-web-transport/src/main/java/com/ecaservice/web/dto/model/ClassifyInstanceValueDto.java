package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_INTEGER_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_0;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1_STRING;

/**
 * Classify instance value dto.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Classify instance value model")
public class ClassifyInstanceValueDto {

    /**
     * Attribute index
     */
    @NotNull
    @Min(VALUE_0)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "Attribute index", minimum = VALUE_1_STRING, maximum = MAX_INTEGER_VALUE_STRING)
    private Integer index;

    /**
     * Attribute value
     */
    @NotNull
    @Schema(description = "Attribute value")
    private BigDecimal value;
}
