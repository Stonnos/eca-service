package com.ecaservice.auto.test.entity.ecaserver;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

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
    private Long id;

    /**
     * Experiment results index
     */
    @Column(name = "results_index", nullable = false, updatable = false)
    private Integer resultsIndex;

    /**
     * Experiment entity
     */
    @ManyToOne
    @JoinColumn(name = "experiment_id", nullable = false, updatable = false)
    private Experiment experiment;
}
