package com.ecaservice.ers.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static com.ecaservice.ers.dto.Constraints.MAX_LENGTH_255;

/**
 * Sort field model.
 *
 * @author Roman Batygin
 */
@Data
public class SortField {

    /**
     * Sort field name
     */
    @NotBlank
    @Size(max = MAX_LENGTH_255)
    @ApiModelProperty(value = "Sort field name")
    private String fieldName;

    /**
     * Sort direction
     */
    @ApiModelProperty(value = "Sort direction")
    private SortDirection direction;
}
