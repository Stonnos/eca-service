package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

import static com.ecaservice.web.dto.util.FieldConstraints.ALPHA_VALUE_MAX_VALUE;
import static com.ecaservice.web.dto.util.FieldConstraints.ALPHA_VALUE_MIN_VALUE;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1;

/**
 * Attributes contingency table request dto.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Attributes contingency table request model")
public class ContingencyTableRequestDto {

    /**
     * Instances id
     */
    @NotNull
    @Min(VALUE_1)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "Instances id")
    private Long instancesId;

    /**
     * X attribute id
     */
    @NotNull
    @Min(VALUE_1)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "X attribute id")
    private Long xAttributeId;

    /**
     * Y attribute id
     */
    @NotNull
    @Min(VALUE_1)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "Y attribute id")
    private Long yAttributeId;

    /**
     * Significant level (alpha value)
     */
    @NotNull
    @DecimalMin(value = ALPHA_VALUE_MIN_VALUE)
    @DecimalMax(value = ALPHA_VALUE_MAX_VALUE)
    @Schema(description = "Significant level (alpha value)", example = "0.05")
    private BigDecimal alphaValue;

    /**
     * Use Yates correct?
     */
    @Schema(description = "Use Yates correct?")
    private boolean useYates;
}
