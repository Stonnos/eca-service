package com.ecaservice.server.exception;

import lombok.NoArgsConstructor;

/**
 * Eca - service base exception class.
 *
 * @author Roman Batygin
 */
@NoArgsConstructor
public class EcaServiceException extends RuntimeException {

    /**
     * Creates exception.
     *
     * @param message error message
     */
    public EcaServiceException(String message) {
        super(message);
    }
}
