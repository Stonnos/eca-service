package com.ecaservice.web.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Page request model.
 *
 * @author Roman Batygin
 */
@Data
public class PageRequestDto {

    /**
     * Page number
     */
    @ApiModelProperty(notes = "Page number")
    private int page;

    /**
     * Page size
     */
    @ApiModelProperty(notes = "Page size")
    private int size;

    /**
     * Sort field
     */
    @ApiModelProperty(notes = "Sort field name")
    private String sortField;

    /**
     * Is ascending sort?
     */
    @ApiModelProperty(notes = "Is ascending sort order?")
    private boolean ascending;

    /**
     * Filters list
     */
    @ApiModelProperty(notes = "Filters list")
    private List<FilterRequestDto> filters;
}
