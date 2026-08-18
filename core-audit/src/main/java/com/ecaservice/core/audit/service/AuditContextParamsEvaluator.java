package com.ecaservice.core.audit.service;

import java.util.Map;

/**
 * Audit context parameters evaluator interface.
 *
 * @author Roman Batygin
 */
public interface AuditContextParamsEvaluator {

    /**
     * Evaluate custom audit context parameters
     *
     * @param methodParams - method input parameters
     * @param returnValue  - method return value
     * @return custom audit context parameters
     */
    Map<String, Object> evaluate(Map<String, Object> methodParams, Object returnValue);
}
