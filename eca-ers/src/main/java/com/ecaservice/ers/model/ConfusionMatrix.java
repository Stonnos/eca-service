package com.ecaservice.ers.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

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
