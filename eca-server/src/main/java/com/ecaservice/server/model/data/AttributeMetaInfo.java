package com.ecaservice.server.model.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Attribute meta info.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttributeMetaInfo {

    /**
     * Attribute name
     */
    private String name;

    /**
     * Attribute type
     */
    private AttributeType type;

    /**
     * Date format for date attribute
     */
    private String dateFormat;

    /**
     * Nominal attribute values
     */
    private List<String> values;
}
