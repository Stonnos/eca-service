package com.ecaservice.data.storage.projection;

import com.ecaservice.data.storage.entity.AttributeType;

/**
 * Attribute info projection.
 *
 * @author Roman Batygin
 */
public interface AttributeInfoProjection {

    /**
     * Gets column name.
     *
     * @return column name
     */
    String getColumnName();

    /**
     * Gets attribute type.
     *
     * @return attribute type
     */
    AttributeType getType();
}
