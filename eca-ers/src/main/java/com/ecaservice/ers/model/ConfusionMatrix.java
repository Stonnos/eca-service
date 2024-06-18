package com.ecaservice.ers.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Confusion matrix persistence entity.
 *
 * @author Roman Batygin
 */
@Getter
@Setter
@Entity
@Table(name = "confusion_matrix")
public class ConfusionMatrix {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Actual class value
     */
    @Column(name = "actual_class", nullable = false)
    private String actualClass;

    /**
     * Expected class value
     */
    @Column(name = "predicted_class", nullable = false)
    private String predictedClass;

    /**
     * Instances number
     */
    @Column(name = "num_instances", nullable = false)
    private Integer numInstances;
}
