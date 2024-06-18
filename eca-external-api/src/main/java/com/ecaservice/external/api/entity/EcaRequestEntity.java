package com.ecaservice.external.api.entity;

import com.ecaservice.base.model.TechnicalStatus;
import com.ecaservice.external.api.dto.EvaluationErrorCode;
import eca.core.evaluation.EvaluationMethod;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import java.time.LocalDateTime;

/**
 * Eca request base entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class EcaRequestEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Correlation id header
     */
    @Column(name = "correlation_id", nullable = false, unique = true)
    private String correlationId;

    /**
     * Request id from eca - server
     */
    @Column(name = "request_id")
    private String requestId;

    /**
     * Request technical status from eca - server
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "technical_status")
    private TechnicalStatus technicalStatus;

    /**
     * Request creation date
     */
    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    /**
     * Request date
     */
    @Column(name = "request_date")
    private LocalDateTime requestDate;

    /**
     * Request processing end date
     */
    @Column(name = "end_date")
    private LocalDateTime endDate;

    /**
     * Request timeout date
     */
    @Column(name = "request_timeout_date")
    private LocalDateTime requestTimeoutDate;

    /**
     * Request stage
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "request_stage")
    private RequestStageType requestStage;

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
     * Error code
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "error_code")
    private EvaluationErrorCode errorCode;

    /**
     * Error message
     */
    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;
}
