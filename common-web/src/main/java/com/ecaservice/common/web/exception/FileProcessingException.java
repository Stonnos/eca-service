package com.ecaservice.common.web.exception;

/**
 * File processing exception.
 *
 * @author Roman Batygin
 */
public class FileProcessingException extends ValidationErrorException {

    public static final String PROCESS_FILE_ERROR_CODE = "ProcessFileError";

    /**
     * Creates exception object.
     *
     * @param message - error message
     */
    public FileProcessingException(String message) {
        super(PROCESS_FILE_ERROR_CODE, message);
    }
}
