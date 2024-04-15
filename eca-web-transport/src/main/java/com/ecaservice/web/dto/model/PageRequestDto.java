package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

import static com.ecaservice.web.dto.util.FieldConstraints.FILTERS_LIST_MAX_LENGTH;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.dto.util.FieldConstraints.SORT_FIELDS_LIST_MAX_LENGTH;

/**
 * Page request model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@Schema(description = "Page request model")
public class PageRequestDto extends SimplePageRequestDto {

    /**
     * Sort fields
     */
    @Valid
    @Size(max = SORT_FIELDS_LIST_MAX_LENGTH)
    @Schema(description = "Sort fields")
    private List<SortFieldRequestDto> sortFields;

    /**
     * Search query string
     */
    @Schema(description = "Search query string")
    @Size(max = MAX_LENGTH_255)
    private String searchQuery;

    /**
     * Filters list
     */
    @Valid
    @Size(max = FILTERS_LIST_MAX_LENGTH)
    @Schema(description = "Filters list")
    private List<FilterRequestDto> filters;

    /**
     * Constructor with parameters.
     *
     * @param page        - page number
     * @param size        - page size
     * @param sortFields  - sort fields
     * @param searchQuery - search query
     * @param filters     - filters list
     */
    public PageRequestDto(Integer page,
                          Integer size,
                          List<SortFieldRequestDto> sortFields,
                          String searchQuery,
                          List<FilterRequestDto> filters) {
        super(page, size);
        this.sortFields = sortFields;
        this.searchQuery = searchQuery;
        this.filters = filters;
    }
}
