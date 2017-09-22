package com.ecaservice.service;

import com.ecaservice.dto.EvaluationResponse;
import com.ecaservice.model.EvaluationRequest;

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
     * @return {@link EvaluationResponse} object
     */
    EvaluationResponse processRequest(EvaluationRequest request);
}
