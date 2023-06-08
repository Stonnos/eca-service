package com.ecaservice.ers.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.ers.dto.ErsErrorCode;

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
        super(ErsErrorCode.RESULTS_NOT_FOUND, message);
    }
}
