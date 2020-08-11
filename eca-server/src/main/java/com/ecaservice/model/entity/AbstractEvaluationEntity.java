package com.ecaservice.model.entity;

import eca.core.evaluation.EvaluationMethod;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * Base evaluation entity.
 *
 * @author Roman Batygin
 */
@Data
@MappedSuperclass
public abstract class AbstractEvaluationEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Request unique identifier
     */
    @Column(name = "request_id")
    private String requestId;

    /**
     * Instance name for request processing (used in cluster environment)
     */
    @Column(name = "instance_name")
    private String instanceName;

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
     * Request status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "request_status")
    private RequestStatus requestStatus;

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
     * Error message
     */
    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;
}
