package com.ecaservice.web.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Experiment type dto.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
public class ExperimentTypeDto {

    /**
     * Experiment type
     */
    @ApiModelProperty(notes = "Experiment algorithm type")
    private String type;

    /**
     * Experiment type description
     */
    @ApiModelProperty(notes = "Experiment description")
    private String description;
}
