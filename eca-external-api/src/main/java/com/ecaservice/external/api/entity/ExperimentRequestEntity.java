package com.ecaservice.external.api.entity;

import com.ecaservice.base.model.ExperimentType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import static com.ecaservice.external.api.util.FieldSize.MODEL_URL_MAX_LENGTH;

/**
 * Experiment request persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "experiment_request")
public class ExperimentRequestEntity extends EcaRequestEntity {

    /**
     * Experiment type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "experiment_type", nullable = false)
    private ExperimentType experimentType;

    /**
     * Experiment download url
     */
    @Column(name = "experiment_download_url", length = MODEL_URL_MAX_LENGTH)
    private String experimentDownloadUrl;
}
