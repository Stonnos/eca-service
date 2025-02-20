package com.ecaservice.server.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

import static com.ecaservice.server.util.FieldConstraints.PRECISION;
import static com.ecaservice.server.util.FieldConstraints.SCALE;

/**
 * Evaluation log persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "evaluation_log")
public class EvaluationLog extends AbstractEvaluationEntity {

    /**
     * Correctly classified percentage
     */
    @Column(name = "pct_correct", precision = PRECISION, scale = SCALE)
    private BigDecimal pctCorrect;

    /**
     * Classifier name
     */
    @Column(name = "classifier_name", nullable = false, updatable = false)
    private String classifierName;

    /**
     * Classifier options json
     */
    @Column(name = "classifier_options", columnDefinition = "text", nullable = false)
    private String classifierOptions;
}
