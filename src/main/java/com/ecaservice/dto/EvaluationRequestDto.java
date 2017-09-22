package com.ecaservice.dto;

import com.ecaservice.model.entity.EvaluationMethod;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

/**
 * Evaluation request transport model.
 * @author Roman Batygin
 */
@Data
@JsonDeserialize(using = EvaluationRequestDeserializer.class)
public class EvaluationRequestDto {

    private AbstractClassifier classifier;

    private Instances data;

    private EvaluationMethod evaluationMethod;

    private Integer numFolds;

    private Integer numTests;
}
