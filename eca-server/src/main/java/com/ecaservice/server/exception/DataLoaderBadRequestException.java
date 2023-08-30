package com.ecaservice.server.exception;

import com.ecaservice.data.loader.dto.DataLoaderApiErrorCode;
import lombok.Getter;

/**
 * Data loader bad request exception class.
 *
 * @author Roman Batygin
 */
public class DataLoaderBadRequestException extends RuntimeException {

    @Getter
    private final DataLoaderApiErrorCode apiErrorCode;

    /**
     * Creates bad request exception.
     *
     * @param apiErrorCode - error code
     * @param message      - error message
     */
    public DataLoaderBadRequestException(DataLoaderApiErrorCode apiErrorCode, String message) {
        super(message);
        this.apiErrorCode = apiErrorCode;
    }
}
