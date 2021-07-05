package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(description = "Page request model")
public class PageRequestDto {

    /**
     * Page number
     */
    @NotNull
    @Min(0)
    @ApiModelProperty(value = "Page number", example = "0")
    private Integer page;

    /**
     * Page size
     */
    @NotNull
    @Min(1)
    @ApiModelProperty(value = "Page size", example = "25")
    private Integer size;

    /**
     * Sort field
     */
    @ApiModelProperty(value = "Sort field")
    private String sortField;

    /**
     * Is ascending sort?
     */
    @ApiModelProperty(value = "Is ascending sort?")
    private boolean ascending;

    /**
     * Search query string
     */
    @ApiModelProperty(value = "Search query string")
    private String searchQuery;

    /**
     * Filters list
     */
    @Valid
    @ApiModelProperty(value = "Filters list")
    private List<FilterRequestDto> filters;
}
