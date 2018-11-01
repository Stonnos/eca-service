package com.ecaservice.web.dto;

import lombok.Data;

import java.util.List;

/**
 * Page dto model.
 *
 * @author Roman Batygin
 */
@Data
public class PageDto<T> {

    /**
     * Page content
     */
    private List<T> content;

    /**
     * Total elements count for page number calculation
     */
    private long totalCount;
}
