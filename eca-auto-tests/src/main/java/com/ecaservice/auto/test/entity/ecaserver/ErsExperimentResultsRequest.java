package com.ecaservice.auto.test.entity.ecaserver;

import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * ERS experiment results request.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "experiment_results_request")
public class ErsExperimentResultsRequest extends ErsRequest {

    /**
     * Experiment results entity
     */
    @ManyToOne
    @JoinColumn(name = "experiment_results_id", nullable = false, unique = true, updatable = false)
    private ExperimentResultsEntity experimentResults;
}
