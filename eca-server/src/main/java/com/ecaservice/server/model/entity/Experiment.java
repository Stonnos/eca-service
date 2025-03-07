package com.ecaservice.server.model.entity;

import com.ecaservice.base.model.ExperimentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

import static com.ecaservice.server.util.FieldConstraints.EXPERIMENT_DOWNLOAD_URL_MAX_LENGTH;
import static com.ecaservice.server.util.FieldConstraints.PRECISION;
import static com.ecaservice.server.util.FieldConstraints.SCALE;

/**
 * Experiment persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "experiment")
public class Experiment extends AbstractEvaluationEntity {

    /**
     * Email
     */
    @Column(name = "email")
    private String email;

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
     * The best classifier correctly classified percentage
     */
    @Column(name = "max_pct_correct", precision = PRECISION, scale = SCALE)
    private BigDecimal maxPctCorrect;
}
