package com.ecaservice.model.entity;

import com.ecaservice.model.EvaluationMethod;
import com.ecaservice.model.EvaluationStatus;
import com.ecaservice.model.ExperimentType;
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

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "sent_date")
    private LocalDateTime sentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "experiment_type", nullable = false)
    private ExperimentType experimentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "evaluation_status", nullable = false)
    private EvaluationStatus evaluationStatus;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    @Enumerated(EnumType.STRING)
    @Column(name = "evaluation_method", nullable = false)
    private EvaluationMethod evaluationMethod;

    @Column(name = "retries_to_sent")
    private int retriesToSent;
}
