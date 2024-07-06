package com.ecaservice.web.dto.model;

/**
 * Field type.
 *
 * @author Roman Batygin
 */
public enum FieldType {

    /**
     * Text field
     */
    TEXT,

    /**
     * Reference field
     */
    REFERENCE,

    /**
     * Integer field
     */
    INTEGER,

    /**
     * Decimal field
     */
    DECIMAL,

    /**
     * Boolean type
     */
    BOOLEAN,

    /**
     * One of object
     */
    ONE_OF_OBJECT,

    /**
     * List objects
     */
    LIST_OBJECTS
}
