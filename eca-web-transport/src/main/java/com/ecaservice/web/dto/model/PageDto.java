package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_INTEGER_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LONG_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_PAGE_SIZE;
import static com.ecaservice.web.dto.util.FieldConstraints.ZERO_VALUE_STRING;

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
    @ArraySchema(schema = @Schema(description = "Page content"), maxItems = MAX_PAGE_SIZE)
    private List<T> content;

    /**
     * Page number
     */
    @Schema(description = "Page number", example = "0", minimum = ZERO_VALUE_STRING, maximum = MAX_INTEGER_VALUE_STRING)
    private int page;

    /**
     * Total elements count in all pages
     */
    @Schema(description = "Total elements count in all pages", example = "1", minimum = ZERO_VALUE_STRING,
            maximum = MAX_LONG_VALUE_STRING)
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
