package com.ecaservice.web.dto;

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
    private String optionName;

    /**
     * Option value
     */
    private String optionValue;
}
