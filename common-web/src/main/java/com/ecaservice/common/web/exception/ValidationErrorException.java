package com.ecaservice.common.web.exception;

import com.ecaservice.common.error.model.ErrorDetails;
import lombok.Getter;

/**
 * Exception throws in case of validation errors.
 *
 * @author Roman Batygin
 */
public class ValidationErrorException extends RuntimeException {

    /**
     * Error code.
     */
    @Getter
    private final ErrorDetails errorDetails;

    /**
     * Field name
     */
    @Getter
    private String fieldName;

    /**
     * Creates exception object.
     *
     * @param errorDetails - error details
     * @param message      - error message
     */
    public ValidationErrorException(ErrorDetails errorDetails, String message) {
        super(message);
        this.errorDetails = errorDetails;
    }

    /**
     * Creates exception object.
     *
     * @param errorDetails - error details
     * @param message      - error message
     * @param fieldName    - field name
     */
    public ValidationErrorException(ErrorDetails errorDetails, String message, String fieldName) {
        this(errorDetails, message);
        this.fieldName = fieldName;
    }
}
