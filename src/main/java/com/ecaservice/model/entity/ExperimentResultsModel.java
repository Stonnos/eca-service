package com.ecaservice.model.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Experiment results model for ERS web - service.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "experiment_results_model")
public class ExperimentResultsModel extends ErsRequest {

    /**
     * Experiment results request
     */
    @ManyToOne
    @JoinColumn(name = "experiment_results_request_id", nullable = false)
    private ExperimentResultsRequest experimentResultsRequest;
}
