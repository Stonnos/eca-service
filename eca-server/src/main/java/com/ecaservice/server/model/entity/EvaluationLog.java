package com.ecaservice.server.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
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
@Table(name = "evaluation_log",
        indexes = {
                @Index(name = "idx_request_id", columnList = "request_id"),
                @Index(name = "idx_evaluation_status", columnList = "request_status")
        }
)
public class EvaluationLog extends AbstractEvaluationEntity {

    /**
     * Classifier info
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "classifier_info_id", nullable = false)
    private ClassifierInfo classifierInfo;

    /**
     * Correctly classified percentage
     */
    @Column(name = "pct_correct", precision = PRECISION, scale = SCALE)
    private BigDecimal pctCorrect;
}
