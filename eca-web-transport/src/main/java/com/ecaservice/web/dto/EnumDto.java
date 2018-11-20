package com.ecaservice.web.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Enum dto.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
public class EnumDto {

    /**
     * Enum value
     */
    @ApiModelProperty(notes = "Enum value")
    private String value;

    /**
     * Enum description
     */
    @ApiModelProperty(notes = "Enum value description")
    private String description;
}
