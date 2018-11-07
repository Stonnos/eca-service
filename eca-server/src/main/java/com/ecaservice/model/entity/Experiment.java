package com.ecaservice.model.entity;

import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.model.experiment.ExperimentType;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Experiment persistence entity.
 *
 * @author Roman Batygin
 */
@Entity
@Table(name = "experiment",
        indexes = {
                @Index(name = "idx_uuid", columnList = "uuid"),
                @Index(name = "idx_experiment_status", columnList = "experiment_status")
        })
@Data
public class Experiment {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * First name
     */
    @Column(name = "first_name")
    private String firstName;

    /**
     * Email
     */
    @Column(name = "email")
    private String email;

    /**
     * Experiment file absolute path
     */
    @Column(name = "experiment_absolute_path")
    private String experimentAbsolutePath;

    /**
     * Training data absolute path
     */
    @Column(name = "training_data_absolute_path")
    private String trainingDataAbsolutePath;

    /**
     * Experiment uuid
     */
    @Column(unique = true)
    private String uuid;

    /**
     * Request creation date
     */
    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    /**
     * Experiment processing start date
     */
    @Column(name = "start_date")
    private LocalDateTime startDate;

    /**
     * Experiment processing end date
     */
    @Column(name = "end_date")
    private LocalDateTime endDate;

    /**
     * Date when experiment results is sent
     */
    @Column(name = "sent_date")
    private LocalDateTime sentDate;

    /**
     * Experiment files deleted date
     */
    @Column(name = "deleted_date")
    private LocalDateTime deletedDate;

    /**
     * Experiment type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "experiment_type")
    private ExperimentType experimentType;

    /**
     * Experiment status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "experiment_status")
    private RequestStatus experimentStatus;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    /**
     * Evaluation method
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "evaluation_method")
    private EvaluationMethod evaluationMethod;
}
