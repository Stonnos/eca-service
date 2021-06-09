package com.ecaservice.code.audit.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Base audit entity.
 *
 * @author Roman Batygin
 */
@Data
@MappedSuperclass
public abstract class BaseAuditEntity {

    /**
     * Audit unit unique identifier
     */
    @Id
    private String id;

    /**
     * Audit unit description string
     */
    private String description;
}
