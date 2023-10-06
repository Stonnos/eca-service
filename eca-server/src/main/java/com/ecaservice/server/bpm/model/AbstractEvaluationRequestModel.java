package com.ecaservice.server.bpm.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Abstract evaluation request model for bpmn process.
 *
 * @author Roman Batygin
 */
@Getter
@Setter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractEvaluationRequestModel implements Serializable {

    /**
     * Request channel
     */
    private String channel;

    /**
     * Request id
     */
    private String requestId;

    /**
     * Train data uuid from central data storage
     */
    private String dataUuid;

    /**
     * Instances table uuid from data storage (for web requests)
     */
    private String instancesUuid;

    /**
     * Username (for web requests)
     */
    private String createdBy;
}
