package com.ecaservice.ers.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.ers.dto.ResponseStatus;

/**
 * Results not found exception class.
 *
 * @author Roman Batygin
 */
public class ResultsNotFoundException extends ValidationErrorException {

    /**
     * Creates results not found exception.
     *
     * @param message - error message
     */
    public ResultsNotFoundException(String message) {
        super(ResponseStatus.RESULTS_NOT_FOUND.name(), message);
    }
}
