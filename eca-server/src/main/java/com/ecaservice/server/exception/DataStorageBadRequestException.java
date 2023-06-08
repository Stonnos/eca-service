package com.ecaservice.server.exception;

import com.ecaservice.data.storage.dto.DsInternalApiErrorCode;
import lombok.Getter;

/**
 * Data storage bad request exception class.
 *
 * @author Roman Batygin
 */
public class DataStorageBadRequestException extends RuntimeException {

    @Getter
    private final DsInternalApiErrorCode dsErrorCode;

    /**
     * Creates data storage bad request exception.
     *
     * @param dsErrorCode - error code
     * @param message     - error message
     */
    public DataStorageBadRequestException(DsInternalApiErrorCode dsErrorCode, String message) {
        super(message);
        this.dsErrorCode = dsErrorCode;
    }
}
