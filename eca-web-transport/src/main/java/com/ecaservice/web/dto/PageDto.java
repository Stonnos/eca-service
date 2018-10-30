package com.ecaservice.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
public class PageDto<T> {

    private List<T> content;
    private long totalCount;

    public static <T> PageDto<T> of(List<T> content, long totalCount) {
        return new PageDto<>(content, totalCount);
    }
}
