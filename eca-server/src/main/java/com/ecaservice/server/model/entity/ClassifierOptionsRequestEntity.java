package com.ecaservice.server.model.entity;

import com.ecaservice.server.model.evaluation.ClassifierOptionsRequestSource;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Optimal classifier options request persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "classifier_options_request")
public class ClassifierOptionsRequestEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Request id
     */
    @Column(name = "request_id")
    private String requestId;

    /**
     * Creation date
     */
    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    /**
     * Request source
     */
    @Enumerated(EnumType.STRING)
    private ClassifierOptionsRequestSource source;

    /**
     * Classifier options request model
     */
    @ManyToOne
    @JoinColumn(name = "classifier_options_request_model_id", nullable = false)
    private ClassifierOptionsRequestModel classifierOptionsRequestModel;
}
