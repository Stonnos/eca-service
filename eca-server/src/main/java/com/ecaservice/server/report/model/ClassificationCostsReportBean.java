package com.ecaservice.server.report.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Classification costs report model.
 *
 * @author Roman Batygin
 */
@Data
public class ClassificationCostsReportBean {

    /**
     * Class value
     */
    private String classValue;

    /**
     * True positive rate
     */
    private BigDecimal truePositiveRate;

    /**
     * False positive rate
     */
    private BigDecimal falsePositiveRate;

    /**
     * True negative rate
     */
    private BigDecimal trueNegativeRate;

    /**
     * False negative rate
     */
    private BigDecimal falseNegativeRate;

    /**
     * Auc value
     */
    private BigDecimal aucValue;
}
