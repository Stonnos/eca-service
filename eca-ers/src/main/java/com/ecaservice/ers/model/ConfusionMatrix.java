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
     * Actual class index
     */
    @Column(name = "actual_class_index", nullable = false)
    private Integer actualClassIndex;

    /**
     * Expected class index
     */
    @Column(name = "predicted_class_index", nullable = false)
    private Integer predictedClassIndex;

    /**
     * Instances number
     */
    @Column(name = "num_instances", nullable = false)
    private Integer numInstances;
}
