package com.ecaservice.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Page dto model.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
public class PageDto<T> {

    /**
     * Page content
     */
    private List<T> content;

    /**
     * Page number
     */
    private int page;

    /**
     * Total elements count for page number calculation
     */
    private long totalCount;

    /**
     * Creates page dto.
     *
     * @param content    - content list
     * @param page       - page number
     * @param totalCount - total elements count for page number calculation
     * @param <T>        - generic type
     * @return page dto
     */
    public static <T> PageDto<T> of(List<T> content, int page, long totalCount) {
        return new PageDto<>(content, page, totalCount);
    }
}
