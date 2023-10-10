package com.ecaservice.server.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import lombok.Getter;

/**
 * Bpmn process error exception.
 *
 * @author Roman Batygin
 */
@Getter
public class BpmnProcessErrorException extends ValidationErrorException {

    /**
     * Creates exception.
     *
     * @param errorCode - error code
     * @param message   - error details
     */
    public BpmnProcessErrorException(String errorCode, String message) {
        super(() -> errorCode, message);
    }
}
