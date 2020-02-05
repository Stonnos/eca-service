package com.ecaservice.web.dto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Page request model.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDto {

    /**
     * Page number
     */
    @NotNull
    @Min(0)
    private Integer page;

    /**
     * Page size
     */
    @NotNull
    @Min(1)
    private Integer size;

    /**
     * Sort field
     */
    private String sortField;

    /**
     * Is ascending sort?
     */
    private boolean ascending;

    /**
     * Search query string
     */
    private String searchQuery;

    /**
     * Filters list
     */
    @Valid
    private List<FilterRequestDto> filters;
}
