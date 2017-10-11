package com.ecaservice.dto;

import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.model.evaluation.EvaluationOption;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.util.Map;

/**
 * Evaluation request transport model.
 *
 * @author Roman Batygin
 */
@Data
@JsonDeserialize(using = EvaluationRequestDeserializer.class)
public class EvaluationRequestDto {

    /**
     * Classifier model
     */
    private AbstractClassifier classifier;

    /**
     * Training data
     */
    private Instances data;

    /**
     * Evaluation method
     */
    private EvaluationMethod evaluationMethod;

    /**
     * Evaluation options map
     */
    private Map<EvaluationOption, String> evaluationOptionsMap;
}
