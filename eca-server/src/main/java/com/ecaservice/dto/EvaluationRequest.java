package com.ecaservice.dto;

import com.ecaservice.dto.json.ClassifierDeserializer;
import com.ecaservice.dto.json.InstancesDeserializer;
import com.ecaservice.model.evaluation.EvaluationOption;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eca.core.evaluation.EvaluationMethod;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Evaluation request transport model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Classifier evaluation request model")
public class EvaluationRequest {

    /**
     * Classifier model
     */
    @NotNull
    @ApiModelProperty(value = "Classifier with specified options", required = true)
    @JsonDeserialize(using = ClassifierDeserializer.class)
    private AbstractClassifier classifier;

    /**
     * Training data
     */
    @NotNull
    @ApiModelProperty(value = "Training data", required = true)
    @JsonDeserialize(using = InstancesDeserializer.class)
    private Instances data;

    /**
     * Evaluation method
     */
    @NotNull
    @ApiModelProperty(value = "Evaluation method", required = true)
    private EvaluationMethod evaluationMethod;

    /**
     * Evaluation options map
     */
    @ApiModelProperty(value = "Evaluation options map")
    private Map<EvaluationOption, String> evaluationOptionsMap;
}
