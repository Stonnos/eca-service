package com.ecaservice.server.service.push.context;

import lombok.Builder;
import lombok.Data;

/**
 * Evaluation push message context model.
 *
 * @author Roman Batygin
 */
@Data
@Builder
public class EvaluationPushMessageContext {

    /**
     * Request id
     */
    private String requestId;

    /**
     * Classifier description
     */
    private String classifierDescription;
}
