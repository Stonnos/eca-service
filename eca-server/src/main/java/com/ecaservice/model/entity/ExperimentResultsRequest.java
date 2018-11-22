package com.ecaservice.model.entity;

import com.ecaservice.model.experiment.ExperimentResultsRequestSource;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Experiment results model for ERS web - service.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "experiment_results_request")
public class ExperimentResultsRequest extends ErsRequest {

    /**
     * Request source
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "request_source")
    private ExperimentResultsRequestSource requestSource;

    /**
     * Experiment entity
     */
    @ManyToOne
    @JoinColumn(name = "experiment_id", nullable = false)
    private Experiment experiment;
}
