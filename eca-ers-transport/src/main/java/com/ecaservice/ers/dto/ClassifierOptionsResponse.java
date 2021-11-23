package com.ecaservice.ers.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.ecaservice.ers.dto.Constraints.UUID_MAX_SIZE;

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
    @Schema(description = "Request id", example = "f8cecbf7-405b-403b-9a94-f51e8fb73ed8", maxLength = UUID_MAX_SIZE)
    private String requestId;

    /**
     * Optimal classifiers reports list
     */
    @Schema(description = "Optimal classifiers reports list")
    private List<ClassifierReport> classifierReports;
}
