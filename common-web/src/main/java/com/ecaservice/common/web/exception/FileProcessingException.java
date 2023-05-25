package com.ecaservice.common.web.exception;

import com.ecaservice.common.web.error.CommonErrorCode;

/**
 * File processing exception.
 *
 * @author Roman Batygin
 */
public class FileProcessingException extends ValidationErrorException {

    /**
     * Creates exception object.
     *
     * @param message - error message
     */
    public FileProcessingException(String message) {
        super(CommonErrorCode.PROCESS_FILE_ERROR, message);
    }
}
