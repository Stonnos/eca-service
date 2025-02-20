package com.ecaservice.server.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;

import static com.ecaservice.server.util.FieldConstraints.PRECISION;
import static com.ecaservice.server.util.FieldConstraints.SCALE;

/**
 * Experiment results persistence entity.
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
     * Experiment results index
     */
    @Column(name = "results_index", nullable = false)
    private Integer resultsIndex;

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

    /**
     * Correctly classified percentage
     */
    @Column(name = "pct_correct", nullable = false, precision = PRECISION, scale = SCALE)
    private BigDecimal pctCorrect;

    /**
     * Experiment entity
     */
    @ManyToOne
    @JoinColumn(name = "experiment_id", nullable = false)
    private Experiment experiment;
}
