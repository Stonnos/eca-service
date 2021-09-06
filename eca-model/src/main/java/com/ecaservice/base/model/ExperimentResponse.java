package com.ecaservice.base.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Experiment response model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExperimentResponse extends EcaResponse {

    /**
     * Request stage
     */
    private RequestStage requestStage;

    /**
     * Experiment download url
     */
    private String downloadUrl;
}
