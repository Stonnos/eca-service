package com.ecaservice.auto.test.entity.ecaserver;

import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * ERS evaluation results request persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "evaluation_results_request")
public class ErsEvaluationResultsRequestEntity extends ErsRequest {

    /**
     * Linked evaluation log
     */
    @OneToOne
    @JoinColumn(name = "evaluation_log_id", nullable = false, unique = true, updatable = false)
    private EvaluationLog evaluationLog;
}
