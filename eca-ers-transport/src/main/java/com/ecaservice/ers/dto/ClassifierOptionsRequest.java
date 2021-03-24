package com.ecaservice.ers.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Classifier options request model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Classifier options request model")
public class ClassifierOptionsRequest {

    /**
     * Instances report
     */
    @Valid
    @NotNull
    @ApiModelProperty(value = "Instances report")
    private InstancesReport instances;

    /**
     * Evaluation method report
     */
    @Valid
    @NotNull
    @ApiModelProperty(value = "Evaluation method report")
    private EvaluationMethodReport evaluationMethodReport;

    /**
     * Sort fields list
     */
    @ApiModelProperty(value = "Sort fields list", allowEmptyValue = true)
    private List<SortField> sortFields;
}
