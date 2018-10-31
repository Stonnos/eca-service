package com.ecaservice.web.dto;

import lombok.Data;

/**
 * Filter request model.
 *
 * @author Roman Batygin
 */
@Data
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
     * Filter type {@link MatchMode}
     */
    private MatchMode matchMode;
}
