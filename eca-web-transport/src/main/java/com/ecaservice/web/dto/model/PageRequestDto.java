package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Size;
import java.util.List;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;

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
public class PageRequestDto extends FilterPageRequestDto {

    /**
     * Sort field
     */
    @Schema(description = "Sort field")
    @Size(max = MAX_LENGTH_255)
    private String sortField;

    /**
     * Is ascending sort?
     */
    @Schema(description = "Is ascending sort?")
    private boolean ascending;

    /**
     * Creates page request dto,
     *
     * @param page        - page number
     * @param size        - page size
     * @param sortField   - sort field
     * @param ascending   - is ascending sort?
     * @param searchQuery - search query string
     * @param filters     - filter fields
     */
    public PageRequestDto(Integer page,
                          Integer size,
                          String sortField,
                          boolean ascending,
                          String searchQuery,
                          List<FilterRequestDto> filters) {
        super(page, size, searchQuery, filters);
        this.sortField = sortField;
        this.ascending = ascending;
    }
}
