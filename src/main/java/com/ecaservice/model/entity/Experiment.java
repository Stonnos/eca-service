package com.ecaservice.model.entity;

import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.model.experiment.ExperimentStatus;
import com.ecaservice.model.experiment.ExperimentType;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Experiment model.
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

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "experiment_absolute_path")
    private String experimentAbsolutePath;

    @Column(name = "training_data_absolute_path")
    private String trainingDataAbsolutePath;

    @Column(unique = true)
    private String uuid;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "sent_date")
    private LocalDateTime sentDate;

    @Column(name = "experiment_type", nullable = false)
    private ExperimentType experimentType;

    @Column(name = "experiment_status", nullable = false)
    private ExperimentStatus experimentStatus;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    @Column(name = "evaluation_method", nullable = false)
    private EvaluationMethod evaluationMethod;

    @Column(name = "retries_to_sent")
    private int retriesToSent;
}
