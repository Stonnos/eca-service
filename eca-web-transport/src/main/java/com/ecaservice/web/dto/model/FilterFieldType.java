package com.ecaservice.web.dto.model;

/**
 * Filter field view type.
 *
 * @author Roman Batygin
 */
public enum FilterFieldType {

    /**
     * Text field filter
     */
    TEXT,

    /**
     * Static reference field filter (used for select item)
     */
    REFERENCE,

    /**
     * Date field filter in format yyyy-MM-dd
     */
    DATE,

    /**
     * Lazy reference field filter (used for lazy loading specific data)
     */
    LAZY_REFERENCE
}
