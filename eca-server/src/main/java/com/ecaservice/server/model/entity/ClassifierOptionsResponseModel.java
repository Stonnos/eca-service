package com.ecaservice.server.model.entity;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Classifier options response model from ERS service.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "classifier_options_response_model")
public class ClassifierOptionsResponseModel {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Classifier name
     */
    @Column(name = "classifier_name")
    private String classifierName;

    /**
     * Classifier description
     */
    @Column(name = "classifier_description")
    private String classifierDescription;

    /**
     * Classifier options config
     */
    @Column(name = "classifier_options", columnDefinition = "text")
    private String options;
}
