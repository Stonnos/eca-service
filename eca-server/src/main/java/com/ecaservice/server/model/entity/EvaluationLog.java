package com.ecaservice.server.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Evaluation log persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "evaluation_log",
        indexes = {
                @Index(name = "idx_request_id", columnList = "request_id"),
                @Index(name = "idx_evaluation_status", columnList = "request_status")
        }
)
public class EvaluationLog extends AbstractEvaluationEntity {

    /**
     * Classifier info
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "classifier_info_id", nullable = false)
    private ClassifierInfo classifierInfo;

    /**
     * Training data info
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "instances_info_id", nullable = false)
    private InstancesInfo instancesInfo;
}
