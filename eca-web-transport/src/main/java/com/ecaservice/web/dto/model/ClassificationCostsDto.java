package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Classification costs dto.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Classification costs model")
public class ClassificationCostsDto {

    /**
     * Class value
     */
    @Schema(description = "Class value", example = "Iris-setosa")
    private String classValue;

    /**
     * TP rate
     */
    @Schema(description = "TP rate", example = "0.75")
    private BigDecimal truePositiveRate;

    /**
     * FP rate
     */
    @Schema(description = "FP rate", example = "0.25")
    private BigDecimal falsePositiveRate;

    /**
     * TN rate
     */
    @Schema(description = "TN rate", example = "0.5")
    private BigDecimal trueNegativeRate;

    /**
     * FN rate
     */
    @Schema(description = "FN rate", example = "0.5")
    private BigDecimal falseNegativeRate;

    /**
     * AUC value
     */
    @Schema(description = "AUC value", example = "0.9")
    private BigDecimal aucValue;
}
