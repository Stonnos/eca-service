package com.ecaservice.server.model.entity;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Classifier info persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "classifier_info")
public class ClassifierInfo {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Classifier name
     */
    @Column(name = "classifier_name", nullable = false)
    private String classifierName;

    /**
     * Classifier options json
     */
    @Column(name = "classifier_options", columnDefinition = "text", nullable = false)
    private String classifierOptions;
}
