package com.ecaservice.server.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Evaluation results request persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "evaluation_results_request",
        indexes = {
                @Index(name = "idx_evaluation_results_request_evaluation_log_id_unique_idx",
                        columnList = "evaluation_log_id")
        })
public class EvaluationResultsRequestEntity extends ErsRequest {

    /**
     * Linked evaluation log
     */
    @OneToOne
    @JoinColumn(name = "evaluation_log_id", nullable = false, unique = true)
    private EvaluationLog evaluationLog;
}
