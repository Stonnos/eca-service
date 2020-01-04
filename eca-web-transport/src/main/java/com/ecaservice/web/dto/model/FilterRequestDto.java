package com.ecaservice.web.dto.model;

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
public class FilterRequestDto {

    /**
     * Column name
     */
    @NotBlank
    private String name;

    /**
     * Column values
     */
    private List<String> values;

    /**
     * Match mode type {@link MatchMode}
     */
    @NotNull
    private MatchMode matchMode;
}
