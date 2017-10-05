package com.ecaservice.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

/**
 * Evaluation options information.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class EvaluationOptions {

    /**
     * Number of folds in k * V cross validation method
     */
    private Integer numFolds;

    /**
     * Number of tests in k * V cross validation method
     */
    private Integer numTests;

}
