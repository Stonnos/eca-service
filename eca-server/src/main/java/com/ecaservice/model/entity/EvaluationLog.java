package com.ecaservice.model.entity;

import eca.core.evaluation.EvaluationMethod;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Evaluation log persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "evaluation_log",
        indexes = {
                @Index(name = "idx_request_id", columnList = "request_id"),
                @Index(name = "idx_evaluation_status", columnList = "evaluation_status")
        }
)
public class EvaluationLog {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Request unique identifier
     */
    @Column(name = "request_id")
    private String requestId;

    /**
     * Request creation date
     */
    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    /**
     * Evaluation start date
     */
    @Column(name = "start_date")
    private LocalDateTime startDate;

    /**
     * Evaluation end date
     */
    @Column(name = "end_date")
    private LocalDateTime endDate;

    /**
     * Classifier info
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "classifier_info_id")
    private ClassifierInfo classifierInfo;

    /**
     * Evaluation status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "evaluation_status")
    private RequestStatus evaluationStatus;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    /**
     * Evaluation method
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "evaluation_method")
    private EvaluationMethod evaluationMethod;

    /**
     * Folds number for k * V cross - validation method
     */
    @Column(name = "num_folds")
    private Integer numFolds;

    /**
     * Tests number for k * V cross - validation method
     */
    @Column(name = "num_tests")
    private Integer numTests;

    /**
     * Seed value for k * V cross - validation method
     */
    private Integer seed;

    /**
     * Training data info
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "instances_info_id")
    private InstancesInfo instancesInfo;

}
