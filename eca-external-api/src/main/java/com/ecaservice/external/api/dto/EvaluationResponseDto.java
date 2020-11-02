package com.ecaservice.external.api.dto;

import lombok.Data;

/**
 * Evaluation response dto.
 *
 * @author Roman Batygin
 */
@Data
public class EvaluationResponseDto {

    /**
     * Request id
     */
    private String requestId;

    /**
     * Request status
     */
    private RequestStatus status;

    /**
     * Classifier model url
     */
    private String modelUrl;
}
