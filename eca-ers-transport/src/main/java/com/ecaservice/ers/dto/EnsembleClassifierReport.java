package com.ecaservice.ers.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.Valid;
import java.util.List;

/**
 * Ensemble classifier report.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@ApiModel(description = "Ensemble classifier report")
public class EnsembleClassifierReport extends ClassifierReport {

    /**
     * Individual classifiers reports
     */
    @Valid
    @ApiModelProperty(value = "Individual classifiers reports")
    private List<ClassifierReport> individualClassifiers;
}
