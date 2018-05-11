package com.ecaservice.model.entity;

import com.ecaservice.dto.evaluation.ResponseStatus;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Evaluation results request persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "evaluation_results_request")
public class EvaluationResultsRequestEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Web - service request date
     */
    @Column(name = "request_date")
    private LocalDateTime requestDate;

    /**
     * Request id
     */
    @Column(name = "request_id")
    private String requestId;

    /**
     * Response status from web - service
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "response_status")
    private ResponseStatus responseStatus;

    /**
     * Error details
     */
    @Lob
    private String details;

    /**
     * Linked evaluation log
     */
    @OneToOne
    @JoinColumn(name = "evaluation_log_id", nullable = false)
    private EvaluationLog evaluationLog;
}
