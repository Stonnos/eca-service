package com.ecaservice.server.exception;

import com.ecaservice.data.storage.dto.DsErrorCode;
import lombok.Getter;

/**
 * Data storage bad request exception class.
 *
 * @author Roman Batygin
 */
public class DataStorageBadRequestException extends RuntimeException {

    @Getter
    private final DsErrorCode dsErrorCode;

    /**
     * Creates data storage bad request exception.
     *
     * @param dsErrorCode - error code
     * @param message     - error message
     */
    public DataStorageBadRequestException(DsErrorCode dsErrorCode, String message) {
        super(message);
        this.dsErrorCode = dsErrorCode;
    }
}
