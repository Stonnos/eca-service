package com.ecaservice.external.api.test.bpm.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;

/**
 * Task result.
 *
 * @author Roman Batygin
 */
@Data
@Builder
public class TaskResult implements Serializable {

    /**
     * Default constructor
     */
    @Tolerate
    public TaskResult() {
    }

    /**
     * Task status
     */
    private BpmTaskStatus status;

    /**
     * Error message
     */
    private String errorMessage;
}
