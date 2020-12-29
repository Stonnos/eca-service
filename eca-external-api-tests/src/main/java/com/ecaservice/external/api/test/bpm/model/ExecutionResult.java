package com.ecaservice.external.api.test.bpm.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Task execution result.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
public class ExecutionResult implements Serializable {

    /**
     * Task execution status
     */
    private TaskExecutionStatus status;

    /**
     * Error message
     */
    private String errorMessage;

    /**
     * Builds execution result.
     *
     * @param status       - task execution status
     * @param errorMessage - error message
     */
    @Builder
    public ExecutionResult(TaskExecutionStatus status, String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
    }
}
