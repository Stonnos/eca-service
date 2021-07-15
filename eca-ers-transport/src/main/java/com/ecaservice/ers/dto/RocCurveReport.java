package com.ecaservice.ers.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;

import static com.ecaservice.ers.dto.Constraints.DECIMAL_MAX_ONE;
import static com.ecaservice.ers.dto.Constraints.DECIMAL_MIN_ZERO;

/**
 * Roc curve report model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Roc curve report model")
public class RocCurveReport {

    /**
     * Auc value
     */
    @DecimalMin(value = DECIMAL_MIN_ZERO)
    @DecimalMax(value = DECIMAL_MAX_ONE)
    @Schema(description = "Auc value", example = "0.9")
    private BigDecimal aucValue;

    /**
     * Specificity value at optimal ROC - curve point
     */
    @DecimalMin(value = DECIMAL_MIN_ZERO)
    @DecimalMax(value = DECIMAL_MAX_ONE)
    @Schema(description = "Specificity value at optimal ROC - curve point", example = "0.2")
    private BigDecimal specificity;

    /**
     * Sensitivity value at optimal ROC - curve point
     */
    @DecimalMin(value = DECIMAL_MIN_ZERO)
    @DecimalMax(value = DECIMAL_MAX_ONE)
    @Schema(description = "Sensitivity value at optimal ROC - curve point", example = "0.8")
    private BigDecimal sensitivity;

    /**
     * Threshold value at optimal ROC - curve point
     */
    @DecimalMin(value = DECIMAL_MIN_ZERO)
    @DecimalMax(value = DECIMAL_MAX_ONE)
    @Schema(description = "Threshold value at optimal ROC - curve point", example = "0.5")
    private BigDecimal thresholdValue;
}
