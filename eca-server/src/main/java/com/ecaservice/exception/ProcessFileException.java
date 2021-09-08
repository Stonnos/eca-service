package com.ecaservice.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;

/**
 * File processing exception.
 *
 * @author Roman Batygin
 */
public class ProcessFileException extends ValidationErrorException {

    public static final String PROCESS_FILE_ERROR_CODE = "ProcessFileError";

    /**
     * Creates exception object.
     *
     * @param message - error message
     */
    public ProcessFileException(String message) {
        super(PROCESS_FILE_ERROR_CODE, message);
    }
}
