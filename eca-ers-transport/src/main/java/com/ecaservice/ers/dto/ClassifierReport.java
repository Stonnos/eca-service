package com.ecaservice.ers.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static com.ecaservice.ers.dto.Constraints.MAX_LENGTH_255;

/**
 * Classifier report model.
 *
 * @author Roman Batygin
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = EnsembleClassifierReport.class, name = "ensemble_classifier_report"),
})
@Data
@ApiModel(description = "Classifier report model")
public class ClassifierReport {

    /**
     * Classifier name
     */
    @NotBlank
    @Size(max = MAX_LENGTH_255)
    @ApiModelProperty(value = "Classifier name", example = "CART")
    private String classifierName;

    /**
     * Classifier description
     */
    @Size(max = MAX_LENGTH_255)
    @ApiModelProperty(value = "Classifier description", allowEmptyValue = true)
    private String classifierDescription;

    /**
     * Classifier options string
     */
    @NotBlank
    @ApiModelProperty(value = "Classifier options string", example = "[classifier options string]")
    private String options;

    /**
     * Classifier input options map
     */
    @ApiModelProperty(value = "Classifier input options map", allowEmptyValue = true)
    private InputOptionsMap inputOptionsMap;

    /**
     * Is meta classifier?
     */
    @ApiModelProperty(value = "Is meta classifier?", allowEmptyValue = true)
    private boolean metaClassifier;
}
