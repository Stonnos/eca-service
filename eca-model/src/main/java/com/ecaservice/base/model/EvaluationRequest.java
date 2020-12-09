package com.ecaservice.base.model;

import com.ecaservice.base.model.databind.ClassifierDeserializer;
import com.ecaservice.base.model.databind.ClassifierSerializer;
import com.ecaservice.base.model.databind.InstancesDeserializer;
import com.ecaservice.base.model.databind.InstancesSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eca.core.evaluation.EvaluationMethod;
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
public class EvaluationRequest {

    /**
     * Classifier model
     */
    @NotNull
    @JsonSerialize(using = ClassifierSerializer.class)
    @JsonDeserialize(using = ClassifierDeserializer.class)
    private AbstractClassifier classifier;

    /**
     * Training data
     */
    @NotNull
    @JsonSerialize(using = InstancesSerializer.class)
    @JsonDeserialize(using = InstancesDeserializer.class)
    private Instances data;

    /**
     * Evaluation method
     */
    @NotNull
    private EvaluationMethod evaluationMethod;

    /**
     * Folds number for k * V cross - validation method
     */
    private Integer numFolds;

    /**
     * Tests number for k * V cross - validation method
     */
    private Integer numTests;

    /**
     * Seed value for k * V cross - validation method
     */
    private Integer seed;
}
