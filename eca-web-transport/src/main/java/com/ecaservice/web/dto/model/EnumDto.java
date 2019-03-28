package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Enum dto model.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Enum dto model")
public class EnumDto {

    /**
     * Enum value
     */
    @ApiModelProperty(value = "Enum value", required = true)
    private String value;

    /**
     * Enum value description
     */
    @ApiModelProperty(value = "Enum value description", required = true)
    private String description;
}
