package com.ecaservice.common.web.exception;

/**
 * Invalid file exception.
 *
 * @author Roman Batygin
 */
public class InvalidFileException extends ValidationErrorException {

    private static final String INVALID_FILE_CODE = "InvalidFile";

    /**
     * Creates exception object.
     *
     * @param message - error message
     */
    public InvalidFileException(String message) {
        super(INVALID_FILE_CODE, message);
    }
}
