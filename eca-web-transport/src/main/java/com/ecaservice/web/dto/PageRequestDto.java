package com.ecaservice.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private int page;

    /**
     * Page size
     */
    private int size;

    /**
     * Sort field
     */
    private String sortField;

    /**
     * Is ascending sort?
     */
    private boolean ascending;

    /**
     * Filters list
     */
    private List<FilterRequestDto> filters;
}
