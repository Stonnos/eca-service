package com.ecaservice.ers.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
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
     * Instances name
     */
    @NotBlank
    @ApiModelProperty(value = "Instances name", example = "iris")
    private String relationName;

    /**
     * Instances MD5 hash sum
     */
    @NotBlank
    @ApiModelProperty(value = "Instances MD5 hash sum", example = "3032e188204cb537f69fc7364f638641")
    private String dataHash;

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
    @Valid
    @ApiModelProperty(value = "Sort fields list")
    private List<SortField> sortFields;
}
