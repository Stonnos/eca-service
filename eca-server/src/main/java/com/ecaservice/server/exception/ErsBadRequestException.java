package com.ecaservice.server.exception;

import com.ecaservice.ers.dto.ErsErrorCode;
import lombok.Getter;

/**
 * ERS bad request exception class.
 *
 * @author Roman Batygin
 */
public class ErsBadRequestException extends RuntimeException {

    @Getter
    private final ErsErrorCode ersErrorCode;

    /**
     * Creates bad request exception.
     *
     * @param ersErrorCode - error code
     * @param message      - error message
     */
    public ErsBadRequestException(ErsErrorCode ersErrorCode, String message) {
        super(message);
        this.ersErrorCode = ersErrorCode;
    }
}
