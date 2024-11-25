package com.ecaservice.data.loader.dto;

import lombok.Data;

import java.util.List;

/**
 * Attribute model.
 *
 * @author Roman Batygin
 */
@Data
public class AttributeInfo {

    /**
     * Attribute name
     */
    private String name;

    /**
     * Attribute type
     */
    private AttributeInfoType type;

    /**
     * Date format for date attribute
     */
    private String dateFormat;

    /**
     * Nominal attribute values
     */
    private List<String> values;
}
