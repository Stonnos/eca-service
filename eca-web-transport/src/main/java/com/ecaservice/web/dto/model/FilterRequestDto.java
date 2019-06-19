package com.ecaservice.web.dto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String name;

    /**
     * Column value
     */
    private String value;

    /**
     * Filter field type {@link FilterFieldType}
     */
    private FilterFieldType filterFieldType;

    /**
     * Match mode type {@link MatchMode}
     */
    private MatchMode matchMode;
}
