package com.ecaservice.server.bpm.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Experiment model for bpmn process.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ExperimentModel extends AbstractEvaluationModel {

    /**
     * Experiment type
     */
    private String experimentType;

    /**
     * Email
     */
    private String email;

    /**
     * Experiment steps number to process.
     */
    private Long stepsCountToProcess;
}
