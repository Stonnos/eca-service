package com.ecaservice.ers.model;

import com.ecaservice.ers.util.FieldSize;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Classification results report persistence entity.
 *
 * @author Roman Batygin
 */
@Getter
@Setter
@Entity
@Table(name = "classification_costs_info")
public class ClassificationCostsInfo {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Class value
     */
    @Column(name = "class_value", nullable = false)
    private String classValue;

    /**
     * TP rate
     */
    @Column(name = "tp_rate", precision = FieldSize.PRECISION, scale = FieldSize.SCALE)
    private BigDecimal truePositiveRate;

    /**
     * FP rate
     */
    @Column(name = "fp_rate", precision = FieldSize.PRECISION, scale = FieldSize.SCALE)
    private BigDecimal falsePositiveRate;

    /**
     * TN rate
     */
    @Column(name = "tn_rate", precision = FieldSize.PRECISION, scale = FieldSize.SCALE)
    private BigDecimal trueNegativeRate;

    /**
     * FN rate
     */
    @Column(name = "fn_rate", precision = FieldSize.PRECISION, scale = FieldSize.SCALE)
    private BigDecimal falseNegativeRate;

    /**
     * Roc - curve info
     */
    @Embedded
    @AttributeOverride(name = "aucValue",
            column = @Column(name = "auc_value", precision = FieldSize.PRECISION, scale = FieldSize.SCALE))
    @AttributeOverride(name = "specificity", column = @Column(precision = FieldSize.PRECISION, scale = FieldSize.SCALE))
    @AttributeOverride(name = "sensitivity", column = @Column(precision = FieldSize.PRECISION, scale = FieldSize.SCALE))
    @AttributeOverride(name = "thresholdValue",
            column = @Column(name = "threshold_value", precision = FieldSize.PRECISION, scale = FieldSize.SCALE))
    private RocCurveInfo rocCurveInfo;
}
