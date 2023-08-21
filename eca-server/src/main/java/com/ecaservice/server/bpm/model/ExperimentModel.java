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
    private RequestStatus requestStatus;

    /**
     * Experiment type
     */
    private ExperimentType experimentType;

    /**
     * Email
     */
    private String email;

    /**
     * Channel.
     */
    private Channel channel;
}
