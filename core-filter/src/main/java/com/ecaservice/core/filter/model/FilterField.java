package com.ecaservice.core.filter.model;

import com.ecaservice.web.dto.model.FilterFieldType;
import com.ecaservice.web.dto.model.MatchMode;
import lombok.Data;

/**
 * Filter field model.
 *
 * @author Roman Batygin
 */
@Data
public class FilterField {

    /**
     * Field name
     */
    private String fieldName;

    /**
     * Field description
     */
    private String description;

    /**
     * Filter field type
     */
    private FilterFieldType filterFieldType;

    /**
     * Filter match mode
     */
    private MatchMode matchMode;

    /**
     * Allow multiple values
     */
    private boolean multiple;

    /**
     * Filter dictionary
     */
    private FilterDictionary dictionary;
}
