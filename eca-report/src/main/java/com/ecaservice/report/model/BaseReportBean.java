package com.ecaservice.report.model;

import lombok.Data;

import java.util.List;

/**
 * Base report model.
 *
 * @author Roman Batygin
 */
@Data
public class BaseReportBean<T> {

    /**
     * Page number
     */
    private int page;

    /**
     * Page size
     */
    private int size;

    /**
     * Total items count
     */
    private int total;

    /**
     * Search query string
     */
    private String searchQuery;

    /**
     * Filters list
     */
    private List<FilterBean> filters;

    /**
     * Report items list
     */
    private List<T> items;
}
