package com.ecaservice.report.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Base report model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
