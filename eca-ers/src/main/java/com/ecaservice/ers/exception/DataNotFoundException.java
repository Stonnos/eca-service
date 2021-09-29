package com.ecaservice.ers.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.ers.dto.ErsErrorCode;

/**
 * Data not found exception class.
 *
 * @author Roman Batygin
 */
public class DataNotFoundException extends ValidationErrorException {

    /**
     * Creates data not found exception.
     *
     * @param message - error message
     */
    public DataNotFoundException(String message) {
        super(ErsErrorCode.DATA_NOT_FOUND.name(), message);
    }
}
