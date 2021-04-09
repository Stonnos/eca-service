package com.ecaservice.model.entity;

import com.ecaservice.base.model.ExperimentType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Experiment persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "experiment",
        indexes = {
                @Index(name = "idx_uuid", columnList = "request_id"),
                @Index(name = "idx_experiment_status", columnList = "request_status")
        })
public class Experiment extends AbstractEvaluationEntity {

    /**
     * First name
     */
    @Column(name = "first_name", nullable = false)
    private String firstName;

    /**
     * Email
     */
    @Column(name = "email", nullable = false)
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
     * Class attribute index
     */
    @Column(name = "class_index")
    private Integer classIndex;

    /**
     * Date when experiment results notification is sent to email service
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
    @Column(name = "experiment_type", nullable = false)
    private ExperimentType experimentType;

    /**
     * Unique token used to download experiment results for external API
     */
    private String token;
}
