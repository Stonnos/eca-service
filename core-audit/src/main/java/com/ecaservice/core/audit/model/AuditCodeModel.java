package com.ecaservice.core.audit.model;

import lombok.Data;

/**
 * Audit code model.
 *
 * @author Roman Batygin
 */
@Data
public class AuditCodeModel {

    /**
     * Audit code
     */
    private String code;

    /**
     * Code title
     */
    private String title;

    /**
     * Audit code enabled?
     */
    private boolean enabled;

    /**
     * Audit group
     */
    private AuditGroupModel auditGroup;
}
