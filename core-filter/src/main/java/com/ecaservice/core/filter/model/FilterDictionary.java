package com.ecaservice.core.filter.model;

import lombok.Data;

import java.util.List;

/**
 * Filter dictionary model.
 *
 * @author Roman Batygin
 */
@Data
public class FilterDictionary {

    /**
     * Dictionary name
     */
    private String name;

    /**
     * Values list for reference filter type
     */
    private List<FilterDictionaryValue> values;
}
