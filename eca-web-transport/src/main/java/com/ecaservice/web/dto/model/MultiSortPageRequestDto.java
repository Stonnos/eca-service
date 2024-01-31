package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

import static com.ecaservice.web.dto.util.FieldConstraints.SORT_FIELDS_LIST_MAX_LENGTH;

/**
 * Multi sort page request model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@Schema(description = "Multi sort page request model")
public class MultiSortPageRequestDto extends FilterPageRequestDto {

    /**
     * Sort fields
     */
    @Valid
    @Size(max = SORT_FIELDS_LIST_MAX_LENGTH)
    @Schema(description = "Sort fields")
    private List<SortFieldRequestDto> sortFields;

    /**
     * Creates page request dto,
     *
     * @param page        - page number
     * @param size        - page size
     * @param sortFields  - sort fields
     * @param searchQuery - search query string
     * @param filters     - filter fields
     */
    public MultiSortPageRequestDto(Integer page,
                                   Integer size,
                                   List<SortFieldRequestDto> sortFields,
                                   String searchQuery,
                                   List<FilterRequestDto> filters) {
        super(page, size, searchQuery, filters);
        this.sortFields = sortFields;
    }
}
