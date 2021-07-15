package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Filter request model.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Filter request model")
public class FilterRequestDto {

    /**
     * Column name
     */
    @NotBlank
    @Schema(description = "Filter column name")
    private String name;

    /**
     * Column values to filter
     */
    @Schema(description = "Column values to filter")
    private List<String> values;

    /**
     * Match mode type {@link MatchMode}
     */
    @NotNull
    @Schema(description = "Match mode type")
    private MatchMode matchMode;
}
