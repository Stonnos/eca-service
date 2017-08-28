package com.ecaservice.model;

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

    private Integer numFolds;

    private Integer numTests;

}
