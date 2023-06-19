package com.ecaservice.server.model.entity;

import com.ecaservice.base.model.ExperimentType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Table;

import static com.ecaservice.server.util.FieldConstraints.EXPERIMENT_DOWNLOAD_URL_MAX_LENGTH;

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
     * User name
     */
    @Column(name = "created_by")
    private String createdBy;

    /**
     * Email
     */
    @Column(name = "email", nullable = false)
    private String email;

    /**
     * Training data file path
     */
    @Column(name = "training_data_path")
    private String trainingDataPath;

    /**
     * Class attribute index
     */
    @Column(name = "class_index")
    private Integer classIndex;

    /**
     * Experiment type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "experiment_type", nullable = false)
    private ExperimentType experimentType;

    /**
     * Model download url
     */
    @Column(name = "experiment_download_url", length = EXPERIMENT_DOWNLOAD_URL_MAX_LENGTH)
    private String experimentDownloadUrl;

    /**
     * Channel type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "channel_type", nullable = false)
    private Channel channel;

    /**
     * Reply to queue
     */
    @Column(name = "reply_to")
    private String replyTo;

    /**
     * MQ message correlation id
     */
    @Column(name = "correlation_id")
    private String correlationId;
}
