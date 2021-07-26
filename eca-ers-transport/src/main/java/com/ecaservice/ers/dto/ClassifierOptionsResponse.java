package com.ecaservice.ers.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * Classifier options response model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Classifier options response model")
public class ClassifierOptionsResponse {

    /**
     * Request id
     */
    @Schema(description = "Request id")
    private String requestId;

    /**
     * Optimal classifiers reports list
     */
    @Schema(description = "Optimal classifiers reports list")
    private List<ClassifierReport> classifierReports;

    /**
     * Response status
     */
    @Schema(description = "Response status")
    private ResponseStatus status;
}
