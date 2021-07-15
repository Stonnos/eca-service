package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Page request model")
public class PageRequestDto {

    /**
     * Page number
     */
    @NotNull
    @Min(0)
    @Schema(description = "Page number", example = "0")
    private Integer page;

    /**
     * Page size
     */
    @NotNull
    @Min(1)
    @Schema(description = "Page size", example = "25")
    private Integer size;

    /**
     * Sort field
     */
    @Schema(description = "Sort field")
    private String sortField;

    /**
     * Is ascending sort?
     */
    @Schema(description = "Is ascending sort?")
    private boolean ascending;

    /**
     * Search query string
     */
    @Schema(description = "Search query string")
    private String searchQuery;

    /**
     * Filters list
     */
    @Valid
    @Schema(description = "Filters list")
    private List<FilterRequestDto> filters;
}
