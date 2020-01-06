package com.ecaservice.dto;

import com.ecaservice.dto.json.ClassifierDeserializer;
import com.ecaservice.dto.json.InstancesDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eca.core.evaluation.EvaluationMethod;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import javax.validation.constraints.NotNull;

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
     * Folds number for k * V cross - validation method
     */
    @ApiModelProperty(value = "Folds number for k * V cross - validation method")
    private Integer numFolds;

    /**
     * Tests number for k * V cross - validation method
     */
    @ApiModelProperty(value = "Tests number for k * V cross - validation method")
    private Integer numTests;

    /**
     * Seed value for k * V cross - validation method
     */
    @ApiModelProperty(value = "Seed value for k * V cross - validation method")
    private Integer seed;
}
