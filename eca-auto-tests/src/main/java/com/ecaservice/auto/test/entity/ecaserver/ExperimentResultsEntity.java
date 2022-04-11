package com.ecaservice.auto.test.entity.ecaserver;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
