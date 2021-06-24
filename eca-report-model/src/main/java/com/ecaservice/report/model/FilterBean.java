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
     * Filter field data as string
     */
    private String filterData;
}
