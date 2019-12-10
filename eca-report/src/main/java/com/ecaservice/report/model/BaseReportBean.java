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

    /**
     * Creates base report bean.
     *
     * @param page        = page number
     * @param totalPages  - total pages
     * @param searchQuery - search query string
     * @param filterBeans - filter beans
     * @param items       - items list
     * @param <T>         item generic type
     * @return base report bean
     */
    public static <T> BaseReportBean<T> of(int page, int totalPages, String searchQuery, List<FilterBean> filterBeans,
                                           List<T> items) {
        return new BaseReportBean<>(page, totalPages, searchQuery, filterBeans, items);
    }
}
