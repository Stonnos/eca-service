package com.ecaservice.core.audit.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Audit context params model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditContextParams {

    /**
     * Audit method input params
     */
    private Map<String, Object> inputParams;

    /**
     * Audit method return value
     */
    private Object returnValue;
}
