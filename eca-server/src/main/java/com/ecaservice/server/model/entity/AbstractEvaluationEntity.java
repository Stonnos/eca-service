package com.ecaservice.server.model.entity;

import eca.core.evaluation.EvaluationMethod;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
    @Column(name = "request_id", nullable = false, unique = true)
    private String requestId;

    /**
     * Training data uuid in data storage (generated while upload data to data loader module)
     */
    @Column(name = "training_data_uuid")
    private String trainingDataUuid;

    /**
     * Training data info
     */
    @ManyToOne
    @JoinColumn(name = "instances_info_id", nullable = false)
    private InstancesInfo instancesInfo;

    /**
     * Channel type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "channel_type", nullable = false)
    private Channel channel;

    /**
     * User name
     */
    @Column(name = "created_by")
    private String createdBy;

    /**
     * Request creation date
     */
    @Column(name = "creation_date", nullable = false)
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
     * Model path in object storage
     */
    @Column(name = "model_path")
    private String modelPath;

    /**
     * Model deleted date
     */
    @Column(name = "deleted_date")
    private LocalDateTime deletedDate;

    /**
     * Request status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "request_status", nullable = false)
    private RequestStatus requestStatus;

    /**
     * Evaluation method
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "evaluation_method", nullable = false)
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
