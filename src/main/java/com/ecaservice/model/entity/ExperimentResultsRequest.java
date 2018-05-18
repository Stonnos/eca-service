package com.ecaservice.model.entity;

import com.ecaservice.model.experiment.ExperimentResultsRequestSource;
import com.ecaservice.model.experiment.ExperimentResultsRequestStatus;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Experiment results request persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "experiment_results_request")
public class ExperimentResultsRequest {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Request creation date
     */
    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    /**
     * Date of successful results sending
     */
    @Column(name = "sent_date")
    private LocalDateTime sentDate;

    /**
     * Request status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "request_status")
    private ExperimentResultsRequestStatus requestStatus;

    /**
     * Request source
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "request_source")
    private ExperimentResultsRequestSource requestSource;

    /**
     * Error details
     */
    @Lob
    private String details;

    /**
     * Linked experiment entity
     */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "experiment_id", nullable = false)
    private Experiment experiment;
}
