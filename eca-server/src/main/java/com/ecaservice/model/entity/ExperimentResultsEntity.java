package com.ecaservice.model.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

import static com.ecaservice.util.FieldConstraints.PRECISION;
import static com.ecaservice.util.FieldConstraints.SCALE;

/**
 * Experiment details persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "experiment_results")
public class ExperimentResultsEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Classifier name
     */
    @Column(name = "classifier_name")
    private String classifierName;

    /**
     * Correctly classified percentage
     */
    @Column(name = "pct_correct", precision = PRECISION, scale = SCALE)
    private BigDecimal pctCorrect;

    /**
     * Experiment entity
     */
    @ManyToOne
    @JoinColumn(name = "experiment_id", nullable = false)
    private Experiment experiment;
}
