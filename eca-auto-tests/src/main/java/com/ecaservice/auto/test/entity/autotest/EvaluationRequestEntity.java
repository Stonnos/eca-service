package com.ecaservice.auto.test.entity.autotest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Evaluation request persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "evaluation_request")
public class EvaluationRequestEntity extends BaseEvaluationRequestEntity {

    /**
     * Classifier name
     */
    @Column(name = "classifier_name")
    private String classifierName;

    /**
     * Classifier options json config
     */
    @Column(name = "classifier_options", columnDefinition = "text")
    private String classifierOptions;

    /**
     * Folds number for k * V cross - validation method
     */
    @Column(name = "num_folds")
    private Integer numFolds;

    /**
     * Tests number for k * V cross - validation method
     */
    @Column(name = "num_tests")
    private Integer numTests;

    /**
     * Seed value for random generator
     */
    private Integer seed;
}
