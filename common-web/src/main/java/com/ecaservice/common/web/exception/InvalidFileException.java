package com.ecaservice.common.web.exception;

import com.ecaservice.common.web.error.CommonErrorCode;

/**
 * Invalid file exception.
 *
 * @author Roman Batygin
 */
public class InvalidFileException extends ValidationErrorException {

    /**
     * Creates exception object.
     *
     * @param message - error message
     */
    public InvalidFileException(String message) {
        super(CommonErrorCode.INVALID_FILE, message);
    }
}
