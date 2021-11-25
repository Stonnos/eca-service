package com.ecaservice.ers.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

import static com.ecaservice.ers.dto.Constraints.DECIMAL_MAX_ONE;
import static com.ecaservice.ers.dto.Constraints.DECIMAL_MIN_ZERO;
import static com.ecaservice.ers.dto.Constraints.MAX_LENGTH_255;
import static com.ecaservice.ers.dto.Constraints.MIN_1;

/**
 * Classification costs report model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Classification costs report model")
public class ClassificationCostsReport {

    /**
     * Class value
     */
    @NotBlank
    @Size(min = MIN_1, max = MAX_LENGTH_255)
    @Schema(description = "Class value", example = "classValue", required = true)
    private String classValue;

    /**
     * True positive rate
     */
    @NotNull
    @DecimalMin(value = DECIMAL_MIN_ZERO)
    @DecimalMax(value = DECIMAL_MAX_ONE)
    @Schema(description = "True positive rate", example = "0.75", required = true)
    private BigDecimal truePositiveRate;

    /**
     * False positive rate
     */
    @NotNull
    @DecimalMin(value = DECIMAL_MIN_ZERO)
    @DecimalMax(value = DECIMAL_MAX_ONE)
    @Schema(description = "False positive rate", example = "0.25", required = true)
    private BigDecimal falsePositiveRate;

    /**
     * True negative rate
     */
    @NotNull
    @DecimalMin(value = DECIMAL_MIN_ZERO)
    @DecimalMax(value = DECIMAL_MAX_ONE)
    @Schema(description = "True negative rate", example = "0.25", required = true)
    private BigDecimal trueNegativeRate;

    /**
     * False negative rate
     */
    @NotNull
    @DecimalMin(value = DECIMAL_MIN_ZERO)
    @DecimalMax(value = DECIMAL_MAX_ONE)
    @Schema(description = "False negative rate", example = "0.5", required = true)
    private BigDecimal falseNegativeRate;

    /**
     * Roc curve report data
     */
    @Valid
    @Schema(description = "Roc curve report data")
    private RocCurveReport rocCurve;
}
