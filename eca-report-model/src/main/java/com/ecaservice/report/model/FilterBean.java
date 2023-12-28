package com.ecaservice.report.model;

import lombok.Data;

/**
 * Filter model.
 *
 * @author Roman Batygin
 */
@Data
public class FilterBean {

    /**
     * Filter field description
     */
    private String description;

    /**
     * Filter field value1 as string
     */
    private String value1;

    /**
     * Filter field value2 as string (used for filter field with {@code MatchMode#RANGE})
     */
    private String value2;
}
