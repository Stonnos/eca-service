package com.ecaservice.ers.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

import static com.ecaservice.ers.dto.Constraints.DECIMAL_MAX_ONE;
import static com.ecaservice.ers.dto.Constraints.DECIMAL_MIN_ZERO;
import static com.ecaservice.ers.dto.Constraints.MAX_LENGTH_255;
import static com.ecaservice.ers.dto.Constraints.MIN_1;
import static com.ecaservice.ers.dto.Constraints.MIN_ZERO;

/**
 * Classification costs report model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Classification costs report model")
public class ClassificationCostsReport {

    /**
     * Class index
     */
    @NotNull
    @Min(MIN_ZERO)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "Class index", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer classIndex;

    /**
     * Class value
     */
    @NotBlank
    @Size(min = MIN_1, max = MAX_LENGTH_255)
    @Schema(description = "Class value", example = "classValue", requiredMode = Schema.RequiredMode.REQUIRED)
    private String classValue;

    /**
     * True positive rate
     */
    @NotNull
    @DecimalMin(value = DECIMAL_MIN_ZERO)
    @DecimalMax(value = DECIMAL_MAX_ONE)
    @Schema(description = "True positive rate", example = "0.75", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal truePositiveRate;

    /**
     * False positive rate
     */
    @NotNull
    @DecimalMin(value = DECIMAL_MIN_ZERO)
    @DecimalMax(value = DECIMAL_MAX_ONE)
    @Schema(description = "False positive rate", example = "0.25", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal falsePositiveRate;

    /**
     * True negative rate
     */
    @NotNull
    @DecimalMin(value = DECIMAL_MIN_ZERO)
    @DecimalMax(value = DECIMAL_MAX_ONE)
    @Schema(description = "True negative rate", example = "0.25", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal trueNegativeRate;

    /**
     * False negative rate
     */
    @NotNull
    @DecimalMin(value = DECIMAL_MIN_ZERO)
    @DecimalMax(value = DECIMAL_MAX_ONE)
    @Schema(description = "False negative rate", example = "0.5", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal falseNegativeRate;

    /**
     * Roc curve report data
     */
    @Valid
    @Schema(description = "Roc curve report data")
    private RocCurveReport rocCurve;
}
