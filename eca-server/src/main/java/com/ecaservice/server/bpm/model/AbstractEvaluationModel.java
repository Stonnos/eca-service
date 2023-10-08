package com.ecaservice.server.bpm.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Abstract evaluation model for bpmn process.
 *
 * @author Roman Batygin
 */
@Data
public class AbstractEvaluationModel implements Serializable {

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
     * Email
     */
    private String email;

    /**
     * Channel.
     */
    private String channel;
}
