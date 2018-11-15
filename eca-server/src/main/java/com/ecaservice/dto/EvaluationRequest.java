package com.ecaservice.dto;

import com.ecaservice.dto.json.ClassifierDeserializer;
import com.ecaservice.dto.json.InstancesDeserializer;
import com.ecaservice.model.evaluation.EvaluationOption;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eca.core.evaluation.EvaluationMethod;
import io.swagger.annotations.ApiModelProperty;
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
public class EvaluationRequest {

    /**
     * Classifier model
     */
    @ApiModelProperty(notes = "Classifier with specified options", required = true)
    @JsonDeserialize(using = ClassifierDeserializer.class)
    private AbstractClassifier classifier;

    /**
     * Training data
     */
    @ApiModelProperty(notes = "Training data", required = true)
    @JsonDeserialize(using = InstancesDeserializer.class)
    private Instances data;

    /**
     * Evaluation method
     */
    @ApiModelProperty(notes = "Evaluation method", required = true)
    private EvaluationMethod evaluationMethod;

    /**
     * Evaluation options map
     */
    @ApiModelProperty(notes = "Evaluation options map")
    private Map<EvaluationOption, String> evaluationOptionsMap;
}
