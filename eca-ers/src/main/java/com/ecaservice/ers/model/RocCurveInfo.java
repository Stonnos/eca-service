package com.ecaservice.ers.model;

import lombok.Data;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

/**
 * Roc - curve info model.
 *
 * @author Roman Batygin
 */
@Data
@Embeddable
public class RocCurveInfo {

    /**
     * Under roc area value
     */
    private BigDecimal aucValue;

    /**
     * Optimal point specificity value
     */
    private BigDecimal specificity;

    /**
     * Optimal point sensitivity value
     */
    private BigDecimal sensitivity;

    /**
     * Optimal point threshold value
     */
    private BigDecimal thresholdValue;
}
