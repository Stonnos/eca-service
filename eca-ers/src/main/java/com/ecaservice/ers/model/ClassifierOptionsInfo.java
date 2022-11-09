package com.ecaservice.ers.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Classifier options info persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "classifier_options_info")
public class ClassifierOptionsInfo {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Classifier name.
     */
    @Column(name = "classifier_name", nullable = false)
    private String classifierName;

    /**
     * Classifier options (input options config)
     */
    @Column(columnDefinition = "text", nullable = false)
    private String options;

    /**
     * Classifier description (additional information).
     */
    @Column(name = "classifier_description")
    private String classifierDescription;
}
