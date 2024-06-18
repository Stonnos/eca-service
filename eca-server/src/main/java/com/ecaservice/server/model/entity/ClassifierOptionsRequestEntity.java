package com.ecaservice.server.model.entity;

import com.ecaservice.server.model.evaluation.ClassifierOptionsRequestSource;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
    @Column(name = "request_id", nullable = false, unique = true)
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
