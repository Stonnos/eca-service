package com.ecaservice.server.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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
