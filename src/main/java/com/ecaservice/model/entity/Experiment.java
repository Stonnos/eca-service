package com.ecaservice.model.entity;

import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.model.experiment.ExperimentStatus;
import com.ecaservice.model.experiment.ExperimentType;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Experiment persistence entity.
 *
 * @author Roman Batygin
 */
@Entity
@Table(name = "experiment")
@Data
public class Experiment {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Request remote ip address
     */
    @Column(name = "ip_address")
    private String ipAddress;

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
     * Email sent date
     */
    @Column(name = "sent_date")
    private LocalDateTime sentDate;

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
    private ExperimentStatus experimentStatus;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    /**
     * Evaluation method
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "evaluation_method")
    private EvaluationMethod evaluationMethod;

    /**
     * Number of failed attempts to send email
     */
    @Column(name = "failed_attempts_to_sent")
    private int failedAttemptsToSent;
}
