package com.ecaservice.server.exception;

import com.ecaservice.server.service.ds.DsInternalErrorCode;
import lombok.Getter;

/**
 * Data storage bad request exception class.
 *
 * @author Roman Batygin
 */
public class DataStorageBadRequestException extends RuntimeException {

    @Getter
    private final DsInternalErrorCode dsInternalErrorCode;

    /**
     * Creates data storage bad request exception.
     *
     * @param dsInternalErrorCode - error code
     * @param message             - error message
     */
    public DataStorageBadRequestException(DsInternalErrorCode dsInternalErrorCode, String message) {
        super(message);
        this.dsInternalErrorCode = dsInternalErrorCode;
    }
}
