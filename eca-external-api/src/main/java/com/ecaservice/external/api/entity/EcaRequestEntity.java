package com.ecaservice.external.api.entity;

import com.ecaservice.base.model.TechnicalStatus;
import eca.core.evaluation.EvaluationMethod;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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
    @Column(name = "technicas_status")
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
     * Error message
     */
    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;
}
