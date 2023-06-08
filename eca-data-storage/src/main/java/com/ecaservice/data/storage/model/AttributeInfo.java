package com.ecaservice.data.storage.model;

import com.ecaservice.data.storage.entity.AttributeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Attribute info model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
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

    /**
     * Attribute values list
     */
    private List<String> values;
}
