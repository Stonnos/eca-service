package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Experiment progress dto model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Experiment progress model")
public class ExperimentProgressDto {

    /**
     * Is experiment processing finished?
     */
    @ApiModelProperty(value = "Is experiment processing finished?")
    private boolean finished;

    /**
     * Experiment progress bar value
     */
    @ApiModelProperty(value = "Experiment progress bar value")
    private Integer progress;
}
