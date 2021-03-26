package com.ecaservice.ers.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Classifier options response model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Classifier options response model")
public class ClassifierOptionsResponse {

    /**
     * Request id
     */
    @ApiModelProperty(value = "Request id")
    private String requestId;

    /**
     * Optimal classifiers reports list
     */
    @ApiModelProperty(value = "Optimal classifiers reports list")
    private List<ClassifierReport> classifierReports;

    /**
     * Response status
     */
    @ApiModelProperty(value = "Response status")
    private ResponseStatus status;
}
