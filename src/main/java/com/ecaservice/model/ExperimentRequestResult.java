package com.ecaservice.model;

import lombok.Data;

/**
 * Experiment request result model.
 *
 * @author Roman Batygin
 */
@Data
public class ExperimentRequestResult {

    private boolean success;

    private String errorMessage;
}
