package com.ecaservice.core.filter.model;

import lombok.Data;

/**
 * Filter field value model. Using for reference filter type
 *
 * @author Roman Batygin
 */
@Data
public class FilterDictionaryValue {

    /**
     * Label string
     */
    private String label;

    /**
     * String value
     */
    private String value;
}
