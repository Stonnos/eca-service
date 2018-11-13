package com.ecaservice.web.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Classifier input option dto model.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
public class InputOptionDto {

    /**
     * Option key
     */
    @ApiModelProperty(notes = "Input option name")
    private String optionName;

    /**
     * Option value
     */
    @ApiModelProperty(notes = "Input option value")
    private String optionValue;
}
