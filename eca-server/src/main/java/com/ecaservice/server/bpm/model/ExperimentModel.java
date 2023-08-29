package com.ecaservice.server.bpm.model;

import com.ecaservice.base.model.ExperimentType;
import com.ecaservice.server.model.entity.Channel;
import com.ecaservice.server.model.entity.RequestStatus;
import lombok.Data;

import java.io.Serializable;

/**
 * Experiment model for bpmn process.
 *
 * @author Roman Batygin
 */
@Data
public class ExperimentModel implements Serializable {

    /**
     * ID
     */
    private Long id;

    /**
     * Request id
     */
    private String requestId;

    /**
     * Request status
     */
    private String requestStatus;

    /**
     * Experiment type
     */
    private String experimentType;

    /**
     * Email
     */
    private String email;

    /**
     * Channel.
     */
    private String channel;

    /**
     * Experiment steps number to process.
     */
    private Long stepsCountToProcess;
}
