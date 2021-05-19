package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Classifier input option dto model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Classifier input option model")
public class InputOptionDto {

    /**
     * Option key
     */
    @ApiModelProperty(value = "Input option name")
    private String optionName;

    /**
     * Option value
     */
    @ApiModelProperty(value = "Input option value")
    private String optionValue;
}
