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
    @Schema(description = "Class value")
    private String classValue;

    /**
     * TP rate
     */
    @Schema(description = "TP rate")
    private BigDecimal truePositiveRate;

    /**
     * FP rate
     */
    @Schema(description = "FP rate")
    private BigDecimal falsePositiveRate;

    /**
     * TN rate
     */
    @Schema(description = "TN rate")
    private BigDecimal trueNegativeRate;

    /**
     * FN rate
     */
    @Schema(description = "FN rate")
    private BigDecimal falseNegativeRate;

    /**
     * AUC value
     */
    @Schema(description = "AUC value")
    private BigDecimal aucValue;
}
