package com.ecaservice.dto;

import com.ecaservice.dto.json.EvaluationRequestDeserializer;
import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.model.evaluation.EvaluationOption;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
@JsonDeserialize(using = EvaluationRequestDeserializer.class)
public class EvaluationRequestDto {

    /**
     * Classifier model
     */
    @ApiModelProperty(notes = "Classifier with specified options", required = true)
    private AbstractClassifier classifier;

    /**
     * Training data
     */
    @ApiModelProperty(notes = "Training data", required = true)
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
