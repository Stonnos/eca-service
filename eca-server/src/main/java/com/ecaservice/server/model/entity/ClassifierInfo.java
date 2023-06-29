package com.ecaservice.server.model.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

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
