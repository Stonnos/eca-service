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
     * Total pages count
     */
    private int totalPages;

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
