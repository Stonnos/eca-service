package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Page dto model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Page model")
public class PageDto<T> {

    /**
     * Page content
     */
    @Schema(description = "Page content")
    private List<T> content;

    /**
     * Page number
     */
    @Schema(description = "Page number", example = "0")
    private int page;

    /**
     * Total elements count in all pages
     */
    @Schema(description = "Total elements count in all pages", example = "1")
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
