package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Experiment results dto model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Experiment results model")
public class ExperimentResultsDto {

    /**
     * Experiment results id
     */
    @ApiModelProperty("Experiment results id")
    private Long id;

    /**
     * Classifier info
     */
    @ApiModelProperty(value = "Classifier info")
    private ClassifierInfoDto classifierInfo;

    /**
     * Experiment results index
     */
    @ApiModelProperty(value = "Results index")
    private Integer resultsIndex;

    /**
     * Correctly classified percentage
     */
    @ApiModelProperty(value = "Correctly classified percentage")
    private BigDecimal pctCorrect;

    /**
     * s experiment results sent to ERS?
     */
    @ApiModelProperty("Is experiment results sent to ERS?")
    private boolean sent;
}