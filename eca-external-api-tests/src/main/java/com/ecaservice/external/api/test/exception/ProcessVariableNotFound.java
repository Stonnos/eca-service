package com.ecaservice.external.api.test.exception;

/**
 * Process variable not found exception.
 *
 * @author Roman Batygin
 */
public class ProcessVariableNotFound extends RuntimeException {

    /**
     * Creates process variable not found exception object.
     *
     * @param variableName - variable name
     * @param processId    - process id
     */
    public ProcessVariableNotFound(String variableName, String processId) {
        super(String.format("No variable [%s] found in process instance [%s]", variableName, processId));
    }
}
