package com.ecaservice.server.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Experiment results model for ERS web - service.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "experiment_results_request",
        indexes = {
                @Index(name = "idx_experiment_results_request_experiment_results_id_unique_idx",
                        columnList = "experiment_results_id")
        })
public class ExperimentResultsRequest extends ErsRequest {

    /**
     * Experiment results entity
     */
    @OneToOne
    @JoinColumn(name = "experiment_results_id", nullable = false, unique = true)
    private ExperimentResultsEntity experimentResults;
}
