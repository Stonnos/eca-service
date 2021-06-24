package com.ecaservice.core.audit.model;

import lombok.Data;

/**
 * Audit group model.
 *
 * @author Roman Batygin
 */
@Data
public class AuditGroupModel {

    /**
     * Group code
     */
    private String groupCode;

    /**
     * Group title
     */
    private String title;
}
