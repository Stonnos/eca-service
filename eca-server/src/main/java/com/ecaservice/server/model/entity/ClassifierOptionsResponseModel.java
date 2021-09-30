package com.ecaservice.server.model.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

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
