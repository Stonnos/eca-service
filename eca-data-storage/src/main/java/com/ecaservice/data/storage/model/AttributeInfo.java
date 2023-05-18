package com.ecaservice.data.storage.model;

import com.ecaservice.data.storage.entity.AttributeType;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Attribute info model.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
public class AttributeInfo {

    /**
     * Column name
     */
    private String columnName;

    /**
     * Attribute type
     */
    private AttributeType type;
}
