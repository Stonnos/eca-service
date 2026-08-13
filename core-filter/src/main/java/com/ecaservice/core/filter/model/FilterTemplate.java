package com.ecaservice.core.filter.model;

import lombok.Data;

import java.util.List;

/**
 * Filter template model.
 *
 * @author Roman Batygin
 */
@Data
public class FilterTemplate {

    /**
     * Template name
     */
    private String templateName;

    /**
     * Template type
     */
    private String templateType;

    /**
     * Filter fields list
     */
    private List<FilterField> fields;

    /**
     * Global filter fields list
     */
    private List<String> globalFilterFields;

    /**
     * Sort fields
     */
    private List<String> sortFields;
}
