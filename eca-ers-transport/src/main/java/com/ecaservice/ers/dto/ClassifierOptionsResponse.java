package com.ecaservice.ers.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Classifier options response model.
 *
 * @author Roman Batygin
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
}
