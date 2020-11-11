package com.ecaservice.external.api.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Evaluation request persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "evaluation_request")
public class EvaluationRequestEntity extends EcaRequestEntity {

    /**
     * Classifier options
     */
    @Column(name = "classifier_options_json", columnDefinition = "text")
    private String classifierOptionsJson;

    /**
     * Classifier model absolute path
     */
    @Column(name = "classifier_absolute_path")
    private String classifierAbsolutePath;

    /**
     * Classifier model file deleted date
     */
    @Column(name = "deleted_date")
    private LocalDateTime deletedDate;
}
