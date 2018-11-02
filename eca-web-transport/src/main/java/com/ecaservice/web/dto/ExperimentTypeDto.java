package com.ecaservice.web.dto;

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
    private String type;

    /**
     * Experiment type description
     */
    private String description;
}
