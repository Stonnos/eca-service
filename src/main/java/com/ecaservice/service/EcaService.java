package com.ecaservice.service;

import com.ecaservice.dto.ClassificationResult;
import com.ecaservice.dto.EvaluationRequest;

/**
 * Eca - service.
 *
 * @author Roman Batygin
 */
public interface EcaService {

    /**
     * Processes input request and returns classification results.
     *
     * @param request {@link EvaluationRequest} object
     * @return {@link ClassificationResult} object
     */
    ClassificationResult processRequest(EvaluationRequest request);
}
